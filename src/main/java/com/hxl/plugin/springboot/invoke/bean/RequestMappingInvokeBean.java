package com.hxl.plugin.springboot.invoke.bean;

import java.util.Objects;

public class RequestMappingInvokeBean  extends InvokeBean{
    private String url;
    private String simpleClassName;
    private String methodName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestMappingInvokeBean)) return false;
        RequestMappingInvokeBean that = (RequestMappingInvokeBean) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static final class RequestMappingInvokeBeanBuilder {
        private String id;
        private String url;
        private String simpleClassName;
        private String methodName;

        private RequestMappingInvokeBeanBuilder() {
        }

        public static RequestMappingInvokeBeanBuilder aRequestMappingInvokeBean() {
            return new RequestMappingInvokeBeanBuilder();
        }

        public RequestMappingInvokeBeanBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public RequestMappingInvokeBeanBuilder withUrl(String url) {
            this.url = url;
            return this;
        }


        public RequestMappingInvokeBeanBuilder withSimpleClassName(String simpleClassName) {
            this.simpleClassName = simpleClassName;
            return this;
        }

        public RequestMappingInvokeBeanBuilder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public RequestMappingInvokeBean build() {
            RequestMappingInvokeBean requestMappingInvokeBean = new RequestMappingInvokeBean();
            requestMappingInvokeBean.setId(id);
            requestMappingInvokeBean.setUrl(url);
            requestMappingInvokeBean.setSimpleClassName(simpleClassName);
            requestMappingInvokeBean.setMethodName(methodName);
            return requestMappingInvokeBean;
        }
    }
}
