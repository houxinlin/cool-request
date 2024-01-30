package com.cool.request.lib.springmvc;

import java.util.Map;

/**
 * 参数推推测后的body
 */
public class JSONObjectGuessBody implements GuessBody{

    private final Map<String, Object> json;

    public JSONObjectGuessBody(Map<String, Object> json) {
        this.json = json;
    }

    public Map<String, Object> getJson() {
        return json;
    }
}
