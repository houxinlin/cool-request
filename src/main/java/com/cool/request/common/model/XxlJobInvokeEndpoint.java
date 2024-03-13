package com.cool.request.common.model;

public class XxlJobInvokeEndpoint  extends SpringInvokeEndpoint{
    private String className;
    private String methodName;
    private String springInnerId;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSpringInnerId() {
        return springInnerId;
    }

    public void setSpringInnerId(String springInnerId) {
        this.springInnerId = springInnerId;
    }

    public static final class XxlJobInvokeEndpointBuilder {
        private String id;
        private String className;
        private String methodName;
        private String springInnerId;

        private XxlJobInvokeEndpointBuilder() {
        }

        public static XxlJobInvokeEndpointBuilder aXxlJobInvokeEndpoint() {
            return new XxlJobInvokeEndpointBuilder();
        }

        public XxlJobInvokeEndpointBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public XxlJobInvokeEndpointBuilder withClassName(String className) {
            this.className = className;
            return this;
        }

        public XxlJobInvokeEndpointBuilder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public XxlJobInvokeEndpointBuilder withSpringInnerId(String springInnerId) {
            this.springInnerId = springInnerId;
            return this;
        }

        public XxlJobInvokeEndpoint build() {
            XxlJobInvokeEndpoint xxlJobInvokeEndpoint = new XxlJobInvokeEndpoint();
            xxlJobInvokeEndpoint.setId(id);
            xxlJobInvokeEndpoint.setClassName(className);
            xxlJobInvokeEndpoint.setMethodName(methodName);
            xxlJobInvokeEndpoint.setSpringInnerId(springInnerId);
            return xxlJobInvokeEndpoint;
        }
    }
}
