package com.cool.request.listener;

import com.cool.request.bean.components.controller.Controller;

public interface SpringBootControllerChooseEvent extends SpringBootComponentSelectedListener {
    /**
     *controller选择事件
     */
    void controllerChooseEvent(Controller select);
}
