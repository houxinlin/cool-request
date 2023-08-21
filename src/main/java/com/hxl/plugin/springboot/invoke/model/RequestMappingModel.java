package com.hxl.plugin.springboot.invoke.model;

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

}
