package com.hxl.plugin.springboot.invoke.model;

import com.hxl.plugin.springboot.invoke.bean.components.scheduled.DynamicSpringScheduled;

import java.util.List;

public class ScheduledModel extends Model {
    public List<DynamicSpringScheduled> scheduledInvokeBeans;
    public int port;

    public List<DynamicSpringScheduled> getScheduledInvokeBeans() {
        return scheduledInvokeBeans;
    }

    public void setScheduledInvokeBeans(List<DynamicSpringScheduled> scheduledInvokeBeans) {
        this.scheduledInvokeBeans = scheduledInvokeBeans;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
