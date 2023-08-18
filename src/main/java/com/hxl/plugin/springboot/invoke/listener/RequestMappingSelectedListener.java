package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.bean.SpringBootScheduledEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpointPlus;
//选择请求映射事件
public interface RequestMappingSelectedListener {
    /**
     *controller选择事件
     */
    public void controllerChooseEvent(SpringMvcRequestMappingEndpointPlus select);

    /**
     * 调度器选择事件
     * @param scheduledEndpoint
     */
    public void scheduledSelectedEvent(SpringBootScheduledEndpoint scheduledEndpoint);
}
