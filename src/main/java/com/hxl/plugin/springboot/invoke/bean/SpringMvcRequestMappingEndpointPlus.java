package com.hxl.plugin.springboot.invoke.bean;

public class SpringMvcRequestMappingEndpointPlus  {
    private String contextPath;
    private int serverPort;

    private SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint;

    public SpringMvcRequestMappingEndpointPlus(String contextPath, int serverPort, SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint) {
        this.contextPath = contextPath;
        this.serverPort = serverPort;
        this.springMvcRequestMappingEndpoint = springMvcRequestMappingEndpoint;
    }

    public SpringMvcRequestMappingEndpoint getSpringMvcRequestMappingEndpoint() {
        return springMvcRequestMappingEndpoint;
    }

    public void setSpringMvcRequestMappingEndpoint(SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint) {
        this.springMvcRequestMappingEndpoint = springMvcRequestMappingEndpoint;
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
