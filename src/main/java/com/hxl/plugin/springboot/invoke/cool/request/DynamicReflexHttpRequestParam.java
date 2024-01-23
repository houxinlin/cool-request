package com.hxl.plugin.springboot.invoke.cool.request;

import com.hxl.plugin.springboot.invoke.bean.components.controller.DynamicController;
import com.hxl.plugin.springboot.invoke.net.request.ReflexHttpRequestParam;

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
