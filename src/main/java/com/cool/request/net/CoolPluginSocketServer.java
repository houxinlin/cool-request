package com.cool.request.net;

import com.cool.request.utils.MessageHandlers;
import com.intellij.openapi.project.Project;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class CoolPluginSocketServer extends IoHandlerAdapter implements Runnable, IoHandler {
    private final MessageHandlers messageHandlers;
    private final Project project;
    private final int port;

    private CoolPluginSocketServer(MessageHandlers messageHandlers, Project project, int port) {
        this.messageHandlers = messageHandlers;
        this.project = project;
        this.port = port;
    }

    public static void start(MessageHandlers messageHandlers, Project project, int port) {
        CoolPluginSocketServer coolPluginSocketServer = new CoolPluginSocketServer(messageHandlers, project, port);
        new Thread(coolPluginSocketServer).start();
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        ByteArrayOutputStream data = (ByteArrayOutputStream) session.getAttribute("data");
        if (data == null) return;
        messageHandlers.handlerMessage(new String(data.toString(StandardCharsets.UTF_8)));
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        cause.printStackTrace();
    }

    @Override
    public void run() {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setHandler(this);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CustomMessageCodecFactory()));

        InetSocketAddress address = new InetSocketAddress(port);
        try {
            acceptor.bind(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class CustomMessageEncoder extends ProtocolEncoderAdapter {
        @Override
        public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        }
    }

    public static class CustomMessageDecoder extends CumulativeProtocolDecoder {

        @Override
        protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
            byte[] data = new byte[in.remaining()];
            ByteArrayOutputStream dataBuffer = (ByteArrayOutputStream) session.getAttribute("data");
            if (dataBuffer == null) dataBuffer = new ByteArrayOutputStream();
            in.get(data);
            dataBuffer.write(data);
            session.setAttribute("data", dataBuffer);
            return false;
        }
    }

    static class CustomMessageCodecFactory implements ProtocolCodecFactory {
        private CustomMessageDecoder customMessageDecoder;

        private CustomMessageEncoder customMessageEncoder;

        public CustomMessageCodecFactory() {
            customMessageDecoder = new CustomMessageDecoder();
            customMessageEncoder = new CustomMessageEncoder();
        }

        @Override
        public ProtocolEncoder getEncoder(IoSession session) throws Exception {
            return customMessageEncoder;
        }

        @Override
        public ProtocolDecoder getDecoder(IoSession session) throws Exception {
            return customMessageDecoder;
        }
    }
}
