package com.cool.request.bean;

public class ScheduleInvokeRequestBody {
    private final String type="scheduled";
    private String id;

    public ScheduleInvokeRequestBody(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
