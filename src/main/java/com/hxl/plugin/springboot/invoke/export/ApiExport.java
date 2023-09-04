package com.hxl.plugin.springboot.invoke.export;

public interface ApiExport {
    boolean export(String json);

    boolean canExport();

    void showCondition();

    boolean checkCookie(String cookie);
}
