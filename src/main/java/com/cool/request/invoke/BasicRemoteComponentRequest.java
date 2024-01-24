package com.cool.request.invoke;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 反射调用基类
 */
public abstract class BasicRemoteComponentRequest<T> implements ProjectComponentRequest<T> {
    public abstract String createMessage(T t);

    private final int port;

    public BasicRemoteComponentRequest(int port) {
        this.port = port;
    }

    @Override
    public InvokeResult request(T t) {
        new Thread(() -> requestSync(t)).start();
        return InvokeResult.SUCCESS;
    }

    @Override
    public InvokeResult requestSync(T invokeData) {
        try (SocketChannel projectSocket = SocketChannel.open(new InetSocketAddress("localhost", port))) {
            // projectSocket.socket().setSoTimeout(0);
            projectSocket.write(StandardCharsets.UTF_8.encode(createMessage(invokeData)));
        } catch (IOException e) {
            return InvokeResult.FAIL;
        }
        return InvokeResult.SUCCESS;
    }
}
