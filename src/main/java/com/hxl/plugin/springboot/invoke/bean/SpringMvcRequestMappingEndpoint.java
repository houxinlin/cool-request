package com.hxl.plugin.springboot.invoke.bean;

import java.util.Objects;

public class SpringMvcRequestMappingEndpoint extends InvokeBean{
    private String url;
    private String simpleClassName;
    private String methodName;
    private String httpMethod;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

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
        if (!(o instanceof SpringMvcRequestMappingEndpoint)) return false;
        SpringMvcRequestMappingEndpoint that = (SpringMvcRequestMappingEndpoint) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
