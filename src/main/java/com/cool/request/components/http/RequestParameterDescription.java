package com.cool.request.components.http;

import java.io.Serializable;

public class RequestParameterDescription implements Serializable {
    private static final long serialVersionUID = 1000000;
    private String name;
    private String type;
    private String description;

    public RequestParameterDescription(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
