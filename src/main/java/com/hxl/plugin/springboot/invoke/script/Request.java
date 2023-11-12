package com.hxl.plugin.springboot.invoke.script;


import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.net.KeyValue;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    private ControllerInvoke.ControllerRequestData controllerRequestData;

    public Request(ControllerInvoke.ControllerRequestData controllerRequestData) {
        this.controllerRequestData = controllerRequestData;
    }

    public void setURL(String newURL) {
        controllerRequestData.setUrl(newURL);
    }

    public String getURL() {
        return controllerRequestData.getUrl();
    }

    public void addHeader(String key, String name) {
        this.controllerRequestData.addHeader(key, name);
    }

    public List<String> getHeader(String key) {
        return this.controllerRequestData.getHeaders().stream()
                .filter(keyValue -> keyValue.getKey().equalsIgnoreCase(key))
                .map(KeyValue::getValue).collect(Collectors.toList());
    }

    public Map<String, String> getQueryParamsMap() {
        Map<String, String> params = new HashMap<>();
        try {
            URI uri = URI.create(this.controllerRequestData.getUrl());
            for (String param : uri.getQuery().split("&")) {
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = "";
                if (pair.length > 1) {
                    value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                }
                params.put(key, value);
            }
        } catch (Exception ignored) {
        }
        return params;
    }

    public String getParam(String key) {
        return getQueryParamsMap().getOrDefault(key, null);
    }
}
