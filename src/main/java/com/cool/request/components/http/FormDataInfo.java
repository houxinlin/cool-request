package com.cool.request.components.http;

import java.io.Serializable;
import java.util.Objects;

public class FormDataInfo extends RequestParameterDescription implements Cloneable, Serializable {
    private static final long serialVersionUID = 1000000;
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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FormDataInfo that = (FormDataInfo) object;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public FormDataInfo clone() {
        return new FormDataInfo(getName(), getValue(), getType());
    }
}
