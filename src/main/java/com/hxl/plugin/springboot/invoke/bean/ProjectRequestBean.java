package com.hxl.plugin.springboot.invoke.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProjectRequestBean {
    private String type;
    private String response;
    private boolean isJson;
    private List<RequestMappingInvokeBean> controller;
    private List<ScheduledInvokeBean> scheduled;
    private int port;

    @JsonProperty("isJson")
    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean json) {
        isJson = json;
    }

    public String getType() {
        return type;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setType(String type) {
        this.type = type;
    }
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
