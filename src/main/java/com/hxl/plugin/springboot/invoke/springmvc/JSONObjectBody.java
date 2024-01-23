package com.hxl.plugin.springboot.invoke.springmvc;

import java.util.Map;

public class JSONObjectBody  implements Body{
    @Override
    public byte[] contentConversion() {
        return new byte[0];
    }

    private final Map<String, Object> json;

    public JSONObjectBody(Map<String, Object> json) {
        this.json = json;
    }

    public Map<String, Object> getJson() {
        return json;
    }
}
