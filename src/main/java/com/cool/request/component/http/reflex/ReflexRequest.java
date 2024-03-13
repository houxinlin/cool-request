package com.cool.request.component.http.reflex;

import com.cool.request.component.http.net.mina.CustomMessageCodecFactory;
import com.cool.request.component.http.net.mina.MessageCallback;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ReflexRequest {
    private final int port;
    private final int waitTime;
    private MessageCallback messageCallback;

    private IoSession ioSession;

    public ReflexRequest(int port, int waitTime, MessageCallback messageCallback) {
        this.port = port;
        this.waitTime = waitTime;
        this.messageCallback = messageCallback;
    }

    public IoSession connection() {
        NioSocketConnector connector = new NioSocketConnector();
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CustomMessageCodecFactory()));
        connector.setHandler(new IoHandlerAdapter() {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                messageCallback.messageReceived(session, message);
            }
        });
        ConnectFuture future = connector.connect(new InetSocketAddress("localhost", port));
        ConnectFuture connectFuture = future.awaitUninterruptibly();
        ioSession = connectFuture.getSession();
        return ioSession;
    }

    public void close() {
        if (ioSession != null) {
            ioSession.closeOnFlush();
        }
    }

    public void request(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        IoBuffer allocate = IoBuffer.allocate(bytes.length + 4);
        allocate.putInt(bytes.length);
        allocate.put(bytes);
        allocate.flip();
        ioSession.write(allocate);
    }
}
