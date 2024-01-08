package com.hxl.plugin.springboot.invoke.bean;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;

public class RequestMappingWrapperFactory {
    public static RequestMappingWrapper getRequestMappingWrapper(SpringMvcRequestMappingSpringInvokeEndpoint controller,
                                                                 RequestMappingModel requestMappingModel, boolean dynamic) {
        if (dynamic)
            return new DynamicRequestMappingWrapper(controller, requestMappingModel.getContextPath(), requestMappingModel.getServerPort(), requestMappingModel.getPort());
        return new StaticRequestMappingWrapper(controller, requestMappingModel.getContextPath(), requestMappingModel.getServerPort(), requestMappingModel.getPort());
    }
}
