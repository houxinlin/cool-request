package com.hxl.plugin.springboot.invoke.springmvc;

public class StringBody  implements Body{
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StringBody(String value) {
        this.value = value;
    }
}
