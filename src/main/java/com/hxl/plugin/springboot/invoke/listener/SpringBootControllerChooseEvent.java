package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;

public interface SpringBootControllerChooseEvent extends SpringBootComponentSelectedListener {
    /**
     *controller选择事件
     */
    void controllerChooseEvent(Controller select);
}
