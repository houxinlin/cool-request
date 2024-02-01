package com.cool.request.utils;

import com.cool.request.common.model.SpringScheduledSpringInvokeEndpoint;

public class SpringScheduledSpringInvokeEndpointWrapper {
    private SpringScheduledSpringInvokeEndpoint springScheduledSpringInvokeEndpoint;
    private int port;

    public SpringScheduledSpringInvokeEndpointWrapper(SpringScheduledSpringInvokeEndpoint springScheduledSpringInvokeEndpoint, int port) {
        this.springScheduledSpringInvokeEndpoint = springScheduledSpringInvokeEndpoint;
        this.port = port;
    }

    public SpringScheduledSpringInvokeEndpoint getSpringScheduledSpringInvokeEndpoint() {
        return springScheduledSpringInvokeEndpoint;
    }

    public void setSpringScheduledSpringInvokeEndpoint(SpringScheduledSpringInvokeEndpoint springScheduledSpringInvokeEndpoint) {
        this.springScheduledSpringInvokeEndpoint = springScheduledSpringInvokeEndpoint;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
