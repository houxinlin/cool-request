package com.hxl.plugin.springboot.invoke.model;

import java.util.Objects;

public class RequestMappingModel  extends Model{
    private int port;
    private SpringMvcRequestMappingInvokeBean controller;
    private int total;
    private int current;
    private int  serverPort;
    private String contextPath;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public SpringMvcRequestMappingInvokeBean getController() {
        return controller;
    }

    public void setController(SpringMvcRequestMappingInvokeBean controller) {
        this.controller = controller;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestMappingModel that = (RequestMappingModel) o;
        return that.getController().getId().equals(this.controller.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.controller.getId());
    }
}
