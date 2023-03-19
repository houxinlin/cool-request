package com.hxl.plugin.springbootschedulerinvoke;

import java.beans.Customizer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class ProjectServerSocket implements Runnable {
    private final PrintStream logPrintStream;
    private final Consumer<DataWrapper> messageConsumer;
    private final Consumer<SocketChannel> closeConsumer;
    private final ServerSocketChannel serverSocketChannel;

    private final int port;

    public ProjectServerSocket(ServerSocketChannel serverSocketChannel, PrintStream logPrintStream, Consumer<DataWrapper> messageConsumer, Consumer<SocketChannel> closeConsumer, int port) {
        this.logPrintStream = logPrintStream;
        this.serverSocketChannel = serverSocketChannel;
        this.messageConsumer = messageConsumer;
        this.closeConsumer = closeConsumer;
        this.port = port;
    }

    @Override
    public void run() {
        for (; ; ) createServer();
    }

    private void createServer() {
        try {
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logPrintStream.println("服务器启动" + port);
            while (true) {
                try {
                    if (selector.select() > 0) {
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();
                            iterator.remove();

                            if (selectionKey.isAcceptable()) {
                                SocketChannel socketChannel = serverSocketChannel.accept();
                                socketChannel.configureBlocking(false);
                                socketChannel.register(selector, SelectionKey.OP_READ);
                            }
                            if (selectionKey.isReadable()) {
                                ByteBuffer sizeByteBuffer = ByteBuffer.allocate(4);
                                //先读取数据大小
                                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                                if (selectionKey.attachment() == null) {
                                    socketChannel.read(sizeByteBuffer);
                                    sizeByteBuffer.flip();
                                    int dataSize = sizeByteBuffer.getInt();
                                    logPrintStream.println("数据大小" + dataSize);
                                    selectionKey.attach(new DataWrapper(dataSize, new ByteArrayOutputStream(), socketChannel));
                                    continue;
                                }
                                DataWrapper attachment = (DataWrapper) selectionKey.attachment();
                                ByteBuffer allocate = ByteBuffer.allocate(1024);
                                int readSize = socketChannel.read(allocate);
                                if (readSize == -1) {
                                    closeConsumer.accept(socketChannel);
                                    logPrintStream.println("客户端关闭");
                                    socketChannel.close();
                                    continue;
                                }
                                allocate.flip();
                                if (readSize > 0) attachment.data.write(allocate.array(), 0, readSize);
                                if (attachment.data.size() == attachment.size) messageConsumer.accept(attachment);

                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(logPrintStream);
                }
            }

        } catch (Exception e) {
            logPrintStream.println(e);
        }
    }
}
