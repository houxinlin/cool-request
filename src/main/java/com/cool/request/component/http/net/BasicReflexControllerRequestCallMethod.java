package com.cool.request.component.http.net;

import com.cool.request.component.http.net.mina.MessageCallback;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;

public abstract class BasicReflexControllerRequestCallMethod extends BasicControllerRequestCallMethod {

    public BasicReflexControllerRequestCallMethod(StandardHttpRequestParam reflexHttpRequestParam) {
        super(reflexHttpRequestParam);
    }

    protected void request(MessageCallback messageCallback, String data, int port) {

    }
}
