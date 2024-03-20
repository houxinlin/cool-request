package com.cool.request.components.http;

public class PullDynamicRequestBody extends ReflexRequestBody {
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getType() {
        return "pull_controller_data";
    }
}
