package com.cool.request.component.http.net.request;

import com.cool.request.common.bean.components.controller.DynamicController;

public class DynamicReflexHttpRequestParam extends ReflexHttpRequestParam {
    private final DynamicController dynamicController;

    public DynamicReflexHttpRequestParam(boolean useProxyObject,
                                         boolean useInterceptor,
                                         boolean userFilter,
                                         DynamicController dynamicController) {
        super(useProxyObject, useInterceptor, userFilter);
        this.dynamicController = dynamicController;
    }

    @Override
    public String getId() {
        return this.dynamicController.getSpringInnerId();
    }
}
