package com.hxl.plugin.springboot.invoke.script;


import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Request {
    private final ControllerRequestData controllerRequestData;

    public Request(ControllerRequestData controllerRequestData) {
        this.controllerRequestData = controllerRequestData;
    }

    public String getId() {
        return this.controllerRequestData.getId();
    }

    public void setUrl(String newURL) {
        controllerRequestData.setUrl(newURL);
    }

    public String getUrl() {
        return URLDecoder.decode(controllerRequestData.getUrl(), StandardCharsets.UTF_8);
    }

    public void addHeader(String key, String name) {
        this.controllerRequestData.addHeader(key, name);
    }

    public List<String> getHeader(String key) {
        return this.controllerRequestData.getHeaders().stream()
                .filter(keyValue -> keyValue.getKey().equalsIgnoreCase(key))
                .map(KeyValue::getValue).collect(Collectors.toList());
    }

    public String getBody() {
        return this.controllerRequestData.getBody();
    }

    public void setBody(byte[] body) {
        this.controllerRequestData.setBody(new String(body, StandardCharsets.UTF_8));
    }

    public void setBody(String body) {
        this.controllerRequestData.setBody(new String(body.getBytes(), StandardCharsets.UTF_8));
    }

    public Map<String, String[]> getUrlParamsMap() {
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

    public String getUrlParam(String key) {
        String[] values = getUrlParamsMap().getOrDefault(key, null);
        if (values != null && values.length >= 1) return values[0];
        return null;
    }

    public String[] getUrlValues(String key) {
        return getUrlParamsMap().getOrDefault(key, new String[]{});
    }

    public void setUrlParam(String key, Object newValues) {
        if (key == null || newValues == null) return;
        if (key.isEmpty()) return;
        String strValues = newValues.toString();
        Map<String, String[]> queryParamsMap = getUrlParamsMap();
        queryParamsMap.remove(key);
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
        if (result.length() > 0 && result.charAt(result.length() - 1) == '&') {
            result.deleteCharAt(result.length() - 1);
        }

        this.controllerRequestData.setUrl(result.toString());
    }

    public Set<String> getHeaderKeys() {
        List<KeyValue> headers = this.controllerRequestData.getHeaders();
        if ( headers== null) return new HashSet<>();
        return headers.stream().map(KeyValue::getKey).collect(Collectors.toSet());
    }
}
