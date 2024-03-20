package com.cool.request.components.http.net.request;


public class DynamicReflexHttpRequestParam extends ReflexHttpRequestParam {
    private final Object attachData;

    public DynamicReflexHttpRequestParam(boolean useProxyObject,
                                         boolean useInterceptor,
                                         boolean userFilter,
                                         Object attachData) {
        super(useProxyObject, useInterceptor, userFilter);
        this.attachData = attachData;
    }

    public Object getAttachData() {
        return attachData;
    }
}
