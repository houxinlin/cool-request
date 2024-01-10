package com.hxl.plugin.springboot.invoke.cool.request;

import com.hxl.plugin.springboot.invoke.bean.components.controller.DynamicController;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;

public class DynamicControllerRequestData extends ControllerRequestData {
    private DynamicController dynamicController;

    public DynamicControllerRequestData(String method, String url, String id,
                                        boolean useProxyObject,
                                        boolean useInterceptor,
                                        boolean userFilter,
                                        DynamicController dynamicController) {
        super(method, url, id, useProxyObject, useInterceptor, userFilter);
        this.dynamicController = dynamicController;
    }

    @Override
    public String getId() {
        return this.dynamicController.getSpringInnerId();
    }
}
