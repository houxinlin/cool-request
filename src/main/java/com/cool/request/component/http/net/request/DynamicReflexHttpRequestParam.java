package com.cool.request.component.http.net.request;

import com.cool.request.common.bean.components.controller.DynamicController;

public class DynamicReflexHttpRequestParam extends ReflexHttpRequestParam {
    private final DynamicController dynamicController;
    private final Object attachData;

    public DynamicReflexHttpRequestParam(boolean useProxyObject,
                                         boolean useInterceptor,
                                         boolean userFilter,
                                         DynamicController dynamicController,
                                         Object attachData) {
        super(useProxyObject, useInterceptor, userFilter);
        this.dynamicController = dynamicController;
        this.attachData = attachData;
    }

    public Object getAttachData() {
        return attachData;
    }

    @Override
    public String getId() {
        return this.dynamicController.getSpringInnerId();
    }
}
