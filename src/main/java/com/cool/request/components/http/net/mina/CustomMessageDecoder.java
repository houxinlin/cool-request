package com.cool.request.components.http.net.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class CustomMessageDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (in.remaining() > 4) {
            in.mark();
            int bodySize = in.getInt();
            if (in.remaining() < bodySize) {
                in.reset();
                return false;
            } else {
                in.reset();
                in.skip(4);
                byte[] bodyBuffer = new byte[bodySize];
                in.get(bodyBuffer, 0, bodySize);

                IoBuffer buffer = IoBuffer.allocate(bodySize);
                buffer.put(bodyBuffer);
                buffer.flip();
                out.write(buffer);
                buffer.free();
                return in.remaining() > 0;
            }
        }
        return false;
    }
}