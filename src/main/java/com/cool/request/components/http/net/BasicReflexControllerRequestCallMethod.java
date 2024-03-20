package com.cool.request.components.http.net;

import com.cool.request.components.http.net.request.StandardHttpRequestParam;

public abstract class BasicReflexControllerRequestCallMethod extends BasicControllerRequestCallMethod {

    public BasicReflexControllerRequestCallMethod(StandardHttpRequestParam reflexHttpRequestParam) {
        super(reflexHttpRequestParam);
    }

    protected void request(String data, int port) {

    }
}
