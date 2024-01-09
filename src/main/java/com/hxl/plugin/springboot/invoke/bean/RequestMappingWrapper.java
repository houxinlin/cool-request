package com.hxl.plugin.springboot.invoke.bean;

import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;

public class RequestMappingWrapper {
    private String moduleName;
    private SpringMvcRequestMappingSpringInvokeEndpoint controller;
    private String contextPath;
    private int port;
    private int pluginPort;

    public RequestMappingWrapper(SpringMvcRequestMappingSpringInvokeEndpoint controller, String contextPath, int port, int pluginPort) {
        this.controller = controller;
        this.contextPath = contextPath;
        this.port = port;
        this.pluginPort = pluginPort;
    }

    public int getPluginPort() {
        return pluginPort;
    }

    public void setPluginPort(int pluginPort) {
        this.pluginPort = pluginPort;
    }

    public SpringMvcRequestMappingSpringInvokeEndpoint getController() {
        return controller;
    }

    public void setController(SpringMvcRequestMappingSpringInvokeEndpoint controller) {
        this.controller = controller;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
