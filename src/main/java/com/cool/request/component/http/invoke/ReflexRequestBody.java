package com.cool.request.component.http.invoke;

public abstract class ReflexRequestBody {
    private String type;

    private String id;

    public String getId() {
        return id;
    }

    public ReflexRequestBody() {
        this.type = getType();
    }

    public void setId(String id) {
        this.id = id;
    }
    public abstract String getType();

    public void setType(String type) {
        this.type = type;
    }
}
