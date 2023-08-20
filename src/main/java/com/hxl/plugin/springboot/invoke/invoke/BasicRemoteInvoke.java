package com.hxl.plugin.springboot.invoke.invoke;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public abstract class BasicRemoteInvoke<T> implements ProjectInvoke<T> {
    public abstract String createMessage(T t);
    private final int port;
    public BasicRemoteInvoke(int port) {
        this.port = port;
    }

    @Override
    public InvokeResult invoke(T t) {
        new Thread(() -> {
            try (SocketChannel projectSocket = SocketChannel.open(new InetSocketAddress("localhost", port));) {
                projectSocket.write(Charset.defaultCharset().encode(createMessage(t)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return InvokeResult.SUCCESS;


    }
}
