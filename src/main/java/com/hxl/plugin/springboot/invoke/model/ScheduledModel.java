package com.hxl.plugin.springboot.invoke.model;

import java.util.List;

public class ScheduledModel  extends Model{
    public List<SpringScheduledSpringInvokeEndpoint> scheduledInvokeBeans;
    public int port;

    public ScheduledModel(List<SpringScheduledSpringInvokeEndpoint> scheduledInvokeBeans, int port) {
        this.scheduledInvokeBeans = scheduledInvokeBeans;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ScheduledModel(List<SpringScheduledSpringInvokeEndpoint> scheduledInvokeBeans) {
        this.scheduledInvokeBeans = scheduledInvokeBeans;
    }

    public ScheduledModel() {
    }

    public List<SpringScheduledSpringInvokeEndpoint> getScheduledInvokeBeans() {
        return scheduledInvokeBeans;
    }

    public void setScheduledInvokeBeans(List<SpringScheduledSpringInvokeEndpoint> scheduledInvokeBeans) {
        this.scheduledInvokeBeans = scheduledInvokeBeans;
    }
}
