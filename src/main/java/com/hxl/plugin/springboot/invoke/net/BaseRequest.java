package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;

public abstract class BaseRequest {
    private ControllerInvoke.InvokeData invokeData;

    public BaseRequest(ControllerInvoke.InvokeData invokeData) {
        this.invokeData = invokeData;
    }

    public ControllerInvoke.InvokeData getInvokeData() {
        return invokeData;
    }

    public abstract  void invoke();
}
