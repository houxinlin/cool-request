package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.hxl.plugin.springboot.invoke.export.ApiExport;

import java.util.HashMap;
import java.util.Map;

/**
 * api??
 */
public class ApiFoxExport implements ApiExport {
    @Override
    public boolean canExport() {
        return true;
    }

    @Override
    public void showCondition() {

    }

    @Override
    public boolean export(String json) {
        Map<String,Object> data =new HashMap<>();
        data.put("importFormat","openapi");
        data.put("apiOverwriteMode","methodAndPath");
        data.put("schemaOverwriteMode","merge");
        data.put("data",json);
        System.out.println();
        return false;
    }
}
