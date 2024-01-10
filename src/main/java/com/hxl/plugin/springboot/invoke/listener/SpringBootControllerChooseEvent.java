package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;

public interface SpringBootControllerChooseEvent extends SpringBootComponentSelectedListener {
    /**
     *controller选择事件
     */
    public void controllerChooseEvent(Controller select);
}
