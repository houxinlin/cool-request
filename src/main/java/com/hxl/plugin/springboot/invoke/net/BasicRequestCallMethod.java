package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.invoke.InvokeException;

public abstract class BasicRequestCallMethod {
    private final ControllerInvoke.ControllerRequestData controllerRequestData;

    public BasicRequestCallMethod(ControllerInvoke.ControllerRequestData controllerRequestData) {
        this.controllerRequestData = controllerRequestData;
    }

    public ControllerInvoke.ControllerRequestData getInvokeData() {
        return controllerRequestData;
    }

    public abstract void invoke() throws InvokeException;
}
