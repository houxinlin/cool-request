package com.hxl.plugin.springboot.invoke.export;

import java.util.Map;

public interface ApiExport {

    boolean export(String json);

    boolean canExport();

    void showCondition();

    Map<String,Boolean> checkToken(ExportCondition  exportCondition);
}
