package com.hxl.plugin.springboot.invoke.net;


import com.hxl.plugin.springboot.invoke.utils.MessageHandlers;
import com.intellij.openapi.project.Project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * 用于插件之间的通信
 */
public class PluginCommunication implements Runnable {
    private final MessageHandlers messageHandlers;
    private Selector selector;
    private Project project;

    public PluginCommunication(Project project, MessageHandlers messageHandlers) {
        this.messageHandlers = messageHandlers;
        this.project = project;
    }

    /**
     * 启动服务
     * @param port 通信端口
     * @throws Exception 异常
     */
    public void startServer(int port) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        new Thread(this).start();
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    private byte[] getByteAndClose(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            channel.close();
            key.cancel();
        } catch (IOException ignored) {
        }
        if (key.attachment() == null) return null;
        return ((ByteArrayOutputStream) key.attachment()).toByteArray();
    }

    private byte[] handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        try {
            int read = channel.read(buffer);
            if (read <= 0) return getByteAndClose(key);
            if (key.attachment() == null) key.attach(new ByteArrayOutputStream());
            ((Buffer) buffer).flip();
            int remainingBytes = buffer.remaining();
            byte[] data = new byte[remainingBytes];
            System.arraycopy(buffer.array(), buffer.position(), data, 0, remainingBytes);
            ((ByteArrayOutputStream) key.attachment()).write(data);
        }catch (Exception e){
            e.printStackTrace();
            channel.close();
            key.cancel();
        }
        return null;
    }

    private void invoke(byte[] data) {
        if (messageHandlers != null) messageHandlers.handlerMessage(new String(data, StandardCharsets.UTF_8));
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        // 处理读取事件
                        byte[] bytes = handleRead(key);
                        if (bytes != null) invoke(bytes);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
