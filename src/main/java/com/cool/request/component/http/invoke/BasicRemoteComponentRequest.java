package com.cool.request.component.http.invoke;


import com.cool.request.utils.GsonUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 反射调用基类
 */
public abstract class BasicRemoteComponentRequest<T extends ReflexRequestBody> implements ProjectComponentRequest<T> {
    public String createMessage(T t) {
        return GsonUtils.toJsonString(t);
    }

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
            ByteBuffer encode = StandardCharsets.UTF_8.encode(createMessage(invokeData));
            byte[] byteArray = Arrays.copyOf(encode.array(), encode.limit());

            ByteBuffer sendBuffer = ByteBuffer.allocate(4 + byteArray.length);
            sendBuffer.putInt(byteArray.length);
            sendBuffer.put(byteArray);
            sendBuffer.flip();
            projectSocket.write(sendBuffer);
        } catch (IOException e) {
            return InvokeResult.FAIL;
        }
        return InvokeResult.SUCCESS;
    }
}
