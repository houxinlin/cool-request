package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.springmvc.RequestParameterDescription;

public class FormDataInfo extends RequestParameterDescription {
    private String name;
    private String value;
    private String type;

    public FormDataInfo(String name, String value, String type) {
        super(name, type, "");
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public FormDataInfo() {
        super("", "", "");
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
