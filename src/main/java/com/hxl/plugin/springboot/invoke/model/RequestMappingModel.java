package com.hxl.plugin.springboot.invoke.model;

import java.util.Set;

public class RequestMappingModel  extends Model{
    private int port;
    private Set<SpringMvcRequestMappingSpringInvokeEndpoint> controller;
    private int total;
    private int  serverPort;
    private String contextPath;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Set<SpringMvcRequestMappingSpringInvokeEndpoint> getController() {
        return controller;
    }

    public void setController( Set<SpringMvcRequestMappingSpringInvokeEndpoint> springMvcRequestMappingInvokeBean) {
        this.controller = springMvcRequestMappingInvokeBean;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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

    public static final class RequestMappingModelBuilder {
        private int port;
        private Set<SpringMvcRequestMappingSpringInvokeEndpoint>  controller;
        private int total;
        private int current;
        private int serverPort;
        private String contextPath;

        private RequestMappingModelBuilder() {
        }

        public static RequestMappingModelBuilder aRequestMappingModel() {
            return new RequestMappingModelBuilder();
        }

        public RequestMappingModelBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        public RequestMappingModelBuilder withRequestMappingInvokeBean(Set<SpringMvcRequestMappingSpringInvokeEndpoint> springMvcRequestMappingInvokeBean) {
            this.controller = springMvcRequestMappingInvokeBean;
            return this;
        }

        public RequestMappingModelBuilder withTotal(int total) {
            this.total = total;
            return this;
        }

        public RequestMappingModelBuilder withCurrent(int current) {
            this.current = current;
            return this;
        }

        public RequestMappingModelBuilder withServerPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public RequestMappingModelBuilder withContextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        public RequestMappingModel build() {
            RequestMappingModel requestMappingModel = new RequestMappingModel();
            requestMappingModel.setPort(port);
            requestMappingModel.setController(controller);
            requestMappingModel.setTotal(total);
            requestMappingModel.setServerPort(serverPort);
            requestMappingModel.setContextPath(contextPath);
            return requestMappingModel;
        }
    }
}
