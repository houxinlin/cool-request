package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.bean.SpringBootScheduledEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpointPlus;
//选择请求映射事件
public interface RequestMappingSelectedListener {
    public void requestMappingSelectedEvent(SpringMvcRequestMappingEndpointPlus select);

    public void scheduledSelectedEvent(SpringBootScheduledEndpoint scheduledEndpoint);
}
