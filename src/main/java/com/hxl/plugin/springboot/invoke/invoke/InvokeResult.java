package com.hxl.plugin.springboot.invoke.invoke;

public enum InvokeResult {
    /**
     *
     */
    SUCCESS("invoke success"),
    FAIL("invoke fail");
    final String message;

    InvokeResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
