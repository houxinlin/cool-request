package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpointPlus;
//选择请求映射事件
public interface RequestMappingSelectedListener {
    public void selectRequestMappingSelectedEvent(SpringMvcRequestMappingEndpointPlus select);
}
