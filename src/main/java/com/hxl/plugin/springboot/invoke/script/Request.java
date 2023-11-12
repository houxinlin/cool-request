package com.hxl.plugin.springboot.invoke.script;


import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.net.KeyValue;

import java.net.MalformedURLException;
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

    public Map<String, String[]> getQueryParamsMap() {
        Map<String, String[]> params = new HashMap<>();
        try {
            URI uri = URI.create(this.controllerRequestData.getUrl());
            if (uri.getRawQuery() == null || "".equalsIgnoreCase(uri.getRawQuery())) return params;
            for (String param : uri.getQuery().split("&")) {
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = "";
                if (pair.length > 1) {
                    value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                }
                params.computeIfAbsent(key, k -> new String[]{});
                String[] oldValues = params.get(key);
                String[] newValues = new String[oldValues.length + 1];
                System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
                newValues[newValues.length - 1] = value;
                params.put(key, newValues);
            }
        } catch (Exception ignored) {
        }
        return params;
    }

    public String getQueryParam(String key) {
        String[] values = getQueryParamsMap().getOrDefault(key, null);
        if (values != null && values.length >= 1) return values[0];
        return null;
    }

    public String[] getQueryValues(String key) {
        return getQueryParamsMap().getOrDefault(key, new String[]{});
    }

    public void setQueryParam(String key, Object newValues) {
        if (key == null || newValues == null) return;
        if (key.isEmpty()) return;
        String strValues = newValues.toString();
        Map<String, String[]> queryParamsMap = getQueryParamsMap();
        String[] oldValues = queryParamsMap.computeIfAbsent(key, k -> new String[]{strValues});

        oldValues[0] = strValues;
        URI uri = URI.create(this.controllerRequestData.getUrl());
        StringBuilder result = new StringBuilder()
                .append(uri.getScheme())
                .append("://")
                .append(uri.getHost());
        if (uri.getPort() != -1) {
            result.append(":").append(uri.getPort());
        }
        result.append(uri.getRawPath());
        result.append("?");
        for (String paramKey : queryParamsMap.keySet()) {
            for (String s : queryParamsMap.get(paramKey)) {
                result.append(paramKey).append("=").append(s).append("&");
            }
        }
        if (result.charAt(result.length() - 1) == '&') {
            result.deleteCharAt(result.length() - 1);
        }
        this.controllerRequestData.setUrl(result.toString());
    }
}
