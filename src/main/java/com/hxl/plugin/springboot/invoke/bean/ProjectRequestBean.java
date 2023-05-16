package com.hxl.plugin.springboot.invoke.bean;

import java.util.List;

public class ProjectRequestBean {
    private List<RequestMappingInvokeBean> controller;
    private List<ScheduledInvokeBean> scheduled;
    private int port;

    public List<ScheduledInvokeBean> getScheduled() {
        return scheduled;
    }

    public void setScheduled(List<ScheduledInvokeBean> scheduled) {
        this.scheduled = scheduled;
    }

    public List<RequestMappingInvokeBean> getController() {
        return controller;
    }

    public void setController(List<RequestMappingInvokeBean> controller) {
        this.controller = controller;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
