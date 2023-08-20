package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledInvokeBean;

import java.util.List;
import java.util.Set;

public interface EndpointListener extends CommunicationListener {
//    public void onEndpoint(int serverPort ,String servletContextPath,Set<SpringMvcRequestMappingEndpoint> mvcRequestMappingEndpoints,
//                           Set<SpringBootScheduledEndpoint> scheduledEndpoints);

    public void onEndpoint(RequestMappingModel springMvcRequestMappingInvokeBean);
    public void onEndpoint(List<SpringScheduledInvokeBean> springMvcRequestMappingInvokeBean);
    public void clear();
}
