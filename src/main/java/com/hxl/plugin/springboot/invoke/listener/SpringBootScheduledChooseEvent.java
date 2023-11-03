package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;

public interface SpringBootScheduledChooseEvent  extends SpringBootComponentSelectedListener{
    /**
     * 调度器选择事件
     */
    public void scheduledChooseEvent(SpringScheduledSpringInvokeEndpoint scheduledEndpoint);
}
