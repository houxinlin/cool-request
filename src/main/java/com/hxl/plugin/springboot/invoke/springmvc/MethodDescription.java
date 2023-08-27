package com.hxl.plugin.springboot.invoke.springmvc;

import java.util.List;

public class MethodDescription {
    private String summary;
    private List<Class<?>> parameters;
    private String description;
    private String methodName;
    private String className;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Class<?>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Class<?>> parameters) {
        this.parameters = parameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
