package com.cool.request.component.http.invoke;

import com.cool.request.component.http.invoke.body.ReflexHttpRequestParamAdapterBody;

public class ReflexControllerRequest extends BasicRemoteComponentRequest<ReflexHttpRequestParamAdapterBody> {
    public ReflexControllerRequest(int port) {
        super(port);
    }

}
