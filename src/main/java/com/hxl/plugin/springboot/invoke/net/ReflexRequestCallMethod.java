package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;

public class ReflexRequestCallMethod extends BasicRequestCallMethod {
    private final int port;

    public ReflexRequestCallMethod(ControllerInvoke.ControllerRequestData controllerRequestData, int port) {
        super(controllerRequestData);
        this.port = port;
    }

    @Override
    public void invoke() {
        ControllerInvoke controllerInvoke = new ControllerInvoke(port);
        controllerInvoke.invoke(getInvokeData());

    }
}
