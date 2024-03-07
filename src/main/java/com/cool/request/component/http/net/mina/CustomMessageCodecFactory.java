package com.cool.request.component.http.net.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class CustomMessageCodecFactory implements ProtocolCodecFactory {
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