package com.cool.request.component.http.net;

import com.cool.request.component.http.net.mina.CustomMessageCodecFactory;
import com.cool.request.view.tool.MessageHandlers;
import com.intellij.openapi.Disposable;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class CoolPluginSocketServer extends IoHandlerAdapter
        implements Runnable, Disposable {
    private final MessageHandlers messageHandlers;
    private final int port;

    private final IoAcceptor acceptor = new NioSocketAcceptor();

    private CoolPluginSocketServer(MessageHandlers messageHandlers, int port) {
        this.messageHandlers = messageHandlers;
        this.port = port;
    }

    public static CoolPluginSocketServer newPluginSocketServer(MessageHandlers messageHandlers, int port) {
        CoolPluginSocketServer coolPluginSocketServer = new CoolPluginSocketServer(messageHandlers, port);
        new Thread(coolPluginSocketServer).start();
        return coolPluginSocketServer;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        byte[] byteBuffer = ((IoBuffer) message).array();
        String str = new String(byteBuffer, StandardCharsets.UTF_8);
        try {
            messageHandlers.handlerMessage(str);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
    }

    @Override
    public void dispose() {
        acceptor.unbind();
        acceptor.dispose();
    }

    @Override
    public void run() {
        acceptor.setHandler(this);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CustomMessageCodecFactory()));
        InetSocketAddress address = new InetSocketAddress(port);
        try {
            acceptor.bind(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        new Thread(() -> {
//            String take = null;
//            while (true) {
//                try {
//                    take = messageHandlerQueue.take();
//                    messageHandlers.handlerMessage(take);
//                } catch (InterruptedException ignored) {
//
//                }
//            }
//        });
    }

}
