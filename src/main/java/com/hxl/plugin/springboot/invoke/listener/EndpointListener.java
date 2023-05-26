package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.bean.SpringBootScheduledEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpoint;

import java.util.Set;

public interface EndpointListener extends CommunicationListener {
    public void onEndpoint(int serverPort ,String servletContextPath,Set<SpringMvcRequestMappingEndpoint> mvcRequestMappingEndpoints,
                           Set<SpringBootScheduledEndpoint> scheduledEndpoints);
}
