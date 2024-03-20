package com.cool.request.components.http.net.mina;

import org.apache.mina.core.session.IoSession;

public interface MessageCallback {
    public void messageReceived(IoSession session, Object message);
}
