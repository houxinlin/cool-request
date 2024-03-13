package com.cool.request.component.http.net.mina;

import org.apache.mina.core.session.IoSession;

public interface MessageCallback {
    public void messageReceived(IoSession session, Object message);
}
