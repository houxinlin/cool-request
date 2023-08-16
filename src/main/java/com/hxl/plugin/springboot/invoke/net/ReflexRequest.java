package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;

public class ReflexRequest extends BaseRequest {
    private int port;

    public ReflexRequest(ControllerInvoke.ControllerRequestData controllerRequestData, int port) {
        super(controllerRequestData);
        this.port = port;
    }

    @Override
    public void invoke() {
        ControllerInvoke controllerInvoke = new ControllerInvoke(port);
        controllerInvoke.invoke(getInvokeData());

    }
}
