package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.invoke.InvokeException;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;

/**
 * 请求发起的方式，http，或者反射
 */
public abstract class BasicControllerRequestCallMethod {
    private final ControllerRequestData controllerRequestData;

    public BasicControllerRequestCallMethod(ControllerRequestData controllerRequestData) {
        this.controllerRequestData = controllerRequestData;
    }

    public ControllerRequestData getInvokeData() {
        return controllerRequestData;
    }

    public abstract void invoke() throws InvokeException;
}
