package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;

import java.util.List;

public interface EndpointListener extends CommunicationListener {
//    public void onEndpoint(int serverPort ,String servletContextPath,Set<SpringMvcRequestMappingEndpoint> mvcRequestMappingEndpoints,
//                           Set<SpringBootScheduledEndpoint> scheduledEndpoints);

    public void onEndpoint(RequestMappingModel SpringMvcRequestMappingSpringInvokeEndpoint);
    public void onEndpoint(List<SpringScheduledSpringInvokeEndpoint> SpringMvcRequestMappingSpringInvokeEndpoint);
    public void clear();
}
