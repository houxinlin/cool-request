package com.hxl.plugin.springboot.invoke.springmvc;

import java.util.Map;

/**
 * 参数推推测后的body
 */
public class JSONObjectBody  implements GuessBody{

    private final Map<String, Object> json;

    public JSONObjectBody(Map<String, Object> json) {
        this.json = json;
    }

    public Map<String, Object> getJson() {
        return json;
    }
}
