package com.cool.request.springmvc;

import java.nio.charset.StandardCharsets;

public class StringBody implements Body {
    @Override
    public byte[] contentConversion() {
        if (value == null) return new byte[]{};
        return value.getBytes(StandardCharsets.UTF_8);
    }

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
