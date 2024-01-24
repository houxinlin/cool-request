package com.cool.request.bean;


import com.cool.request.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.cool.request.model.SpringScheduledSpringInvokeEndpoint;

import java.util.List;
import java.util.Map;

public class ProjectRequestBean {
    private String type;
    private String response;
    private List<SpringMvcRequestMappingSpringInvokeEndpoint> controller;
    private List<SpringScheduledSpringInvokeEndpoint> scheduled;
    private int port;
    private int serverPort;
    private String contextPath;
    private String id;
    private Map<String, List<String>> responseHeaders;

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }
    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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

    public List<SpringMvcRequestMappingSpringInvokeEndpoint> getController() {
        return controller;
    }

    public void setController(List<SpringMvcRequestMappingSpringInvokeEndpoint> controller) {
        this.controller = controller;
    }

    public List<SpringScheduledSpringInvokeEndpoint> getScheduled() {
        return scheduled;
    }

    public void setScheduled(List<SpringScheduledSpringInvokeEndpoint> scheduled) {
        this.scheduled = scheduled;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
