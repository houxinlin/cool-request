package com.hxl.plugin.springboot.invoke.bean;

import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;

public class StaticRequestMappingWrapper  extends RequestMappingWrapper{
    public StaticRequestMappingWrapper(SpringMvcRequestMappingSpringInvokeEndpoint controller, String contextPath, int port, int pluginPort) {
        super(controller, contextPath, port, pluginPort);
    }
}
