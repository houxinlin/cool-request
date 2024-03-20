package com.cool.request.components.http;

import java.io.Serializable;

public class KeyValue implements Cloneable, Serializable {
    private static final long serialVersionUID = 1000000;
    private String key;
    private String value;
    private String valueType = "string";
    private String describe = "";

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValue(String key, String value, String valueType) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
    }

    public KeyValue(String key, String value, String valueType, String describe) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public KeyValue() {
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public KeyValue clone() {
        return new KeyValue(getKey(), getValue());
    }
}
