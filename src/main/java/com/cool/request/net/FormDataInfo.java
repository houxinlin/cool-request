package com.cool.request.net;

import com.cool.request.springmvc.RequestParameterDescription;

public class FormDataInfo extends RequestParameterDescription implements Cloneable {
    private String value;

    public FormDataInfo(String name, String value, String type) {
        super(name, type, "");
        this.value = value;
    }

    public FormDataInfo() {
        super("", "", "");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public FormDataInfo clone() {
        return new FormDataInfo(getName(), getValue(), getType());
    }
}
