package com.cool.request.cool.request;

import com.cool.request.bean.components.controller.DynamicController;
import com.cool.request.net.request.ReflexHttpRequestParam;

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
