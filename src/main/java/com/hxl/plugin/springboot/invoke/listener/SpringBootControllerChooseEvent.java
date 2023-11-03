package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;

public interface SpringBootControllerChooseEvent extends SpringBootComponentSelectedListener {
    /**
     *controller选择事件
     */
    public void controllerChooseEvent(RequestMappingModel select);
}
