package com.hxl.plugin.springboot.invoke.bean;

import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;

public class DynamicRequestMappingWrapper  extends RequestMappingWrapper{
    public DynamicRequestMappingWrapper(SpringMvcRequestMappingSpringInvokeEndpoint controller, String contextPath, int port, int pluginPort) {
        super(controller, contextPath, port, pluginPort);
    }
}
