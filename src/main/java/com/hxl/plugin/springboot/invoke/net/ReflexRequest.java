package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;

public class ReflexRequest extends BaseRequest {
    private int port;

    public ReflexRequest(ControllerInvoke.InvokeData invokeData, int port) {
        super(invokeData);
        this.port = port;
    }

    @Override
    public void invoke() {
        ControllerInvoke controllerInvoke = new ControllerInvoke(port);
        controllerInvoke.invoke(getInvokeData());

    }
}
