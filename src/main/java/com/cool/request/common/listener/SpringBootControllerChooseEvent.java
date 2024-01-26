package com.cool.request.common.listener;

import com.cool.request.common.bean.components.controller.Controller;

public interface SpringBootControllerChooseEvent extends SpringBootComponentSelectedListener {
    /**
     *controller选择事件
     */
    void controllerChooseEvent(Controller select);
}
