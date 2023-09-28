package com.hxl.plugin.springboot.invoke.model;

public class RequestMappingModel  extends Model{
    private int port;
    private SpringMvcRequestMappingSpringInvokeEndpoint controller;
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

    public SpringMvcRequestMappingSpringInvokeEndpoint getController() {
        return controller;
    }

    public void setController(SpringMvcRequestMappingSpringInvokeEndpoint SpringMvcRequestMappingSpringInvokeEndpoint) {
        this.controller = SpringMvcRequestMappingSpringInvokeEndpoint;
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

    public static final class RequestMappingModelBuilder {
        private int port;
        private SpringMvcRequestMappingSpringInvokeEndpoint SpringMvcRequestMappingSpringInvokeEndpoint;
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

        public RequestMappingModelBuilder withRequestMappingInvokeBean(SpringMvcRequestMappingSpringInvokeEndpoint SpringMvcRequestMappingSpringInvokeEndpoint) {
            this.SpringMvcRequestMappingSpringInvokeEndpoint = SpringMvcRequestMappingSpringInvokeEndpoint;
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
            requestMappingModel.setController(SpringMvcRequestMappingSpringInvokeEndpoint);
            requestMappingModel.setTotal(total);
            requestMappingModel.setCurrent(current);
            requestMappingModel.setServerPort(serverPort);
            requestMappingModel.setContextPath(contextPath);
            return requestMappingModel;
        }
    }
}
