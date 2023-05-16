package com.hxl.plugin.springboot.invoke.invoke;

public enum InvokeResult {
    SUCCESS("调用成功"),
    FAIL("调用失败");

    String message;

    InvokeResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
