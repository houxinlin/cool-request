package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledInvokeBean;

//选择请求映射事件
public interface RequestMappingSelectedListener {
    /**
     *controller选择事件
     */
    public void controllerChooseEvent(RequestMappingModel select);
    /**
     * 调度器选择事件
     */
    public void scheduledChooseEvent(SpringScheduledInvokeBean scheduledEndpoint);
}
