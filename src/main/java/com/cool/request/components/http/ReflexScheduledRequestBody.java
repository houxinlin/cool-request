package com.cool.request.components.http;

public class ReflexScheduledRequestBody extends ReflexRequestBody {
    private String param;

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String getType() {
        return "scheduled";
    }
}
