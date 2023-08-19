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
        try (SocketChannel projectSocket = SocketChannel.open(new InetSocketAddress("localhost", this.port));) {
            projectSocket.write(Charset.defaultCharset().encode(createMessage(t)));
            return InvokeResult.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return InvokeResult.FAIL;

    }
}
