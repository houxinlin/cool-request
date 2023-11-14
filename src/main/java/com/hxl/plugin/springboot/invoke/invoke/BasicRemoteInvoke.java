package com.hxl.plugin.springboot.invoke.invoke;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BasicRemoteInvoke<T> implements ProjectInvoke<T> {
    public abstract String createMessage(T t);

    private final int port;

    public BasicRemoteInvoke(int port) {
        this.port = port;
    }

    @Override
    public InvokeResult invoke(T t) {
        new Thread(() -> invokeSync(t)).start();
        return InvokeResult.SUCCESS;
    }

    @Override
    public InvokeResult invokeSync(T invokeData) {
        try (SocketChannel projectSocket = SocketChannel.open(new InetSocketAddress("localhost", port));) {
            projectSocket.write(StandardCharsets.UTF_8.encode(createMessage(invokeData)));
        } catch (IOException e) {
            return InvokeResult.FAIL;
        }
        return InvokeResult.SUCCESS;
    }
}
