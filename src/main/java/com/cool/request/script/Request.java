package com.cool.request.script;

import com.cool.request.net.KeyValue;
import com.cool.request.net.request.StandardHttpRequestParam;
import com.cool.request.springmvc.StringBody;
import com.cool.request.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.script.HTTPRequest;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request implements HTTPRequest {
    private final StandardHttpRequestParam standardHttpRequestParam;

    public Request(StandardHttpRequestParam standardHttpRequestParam) {
        this.standardHttpRequestParam = standardHttpRequestParam;
    }

    @Override
    public List<String> getHeaders(String key) {
        return this.standardHttpRequestParam.getHeaders().stream()
                .filter(keyValue -> keyValue.getKey().equalsIgnoreCase(key))
                .map(KeyValue::getValue).collect(Collectors.toList());
    }

    @Override
    public String getParameter(String key) {
        List<String> values = getParameterMap().getOrDefault(key, null);
        if (values != null && values.size() >= 1) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public void setParameter(String key, String newValues) {
        if (StringUtils.isEmpty(key)) return;
        if (newValues == null) return;
        String strValues = newValues.toString();
        Map<String, List<String>> queryParamsMap = getParameterMap();
        queryParamsMap.remove(key);
        List<String> newParam = new ArrayList<>();
        newParam.add(strValues);
        List<String> oldValues = queryParamsMap.computeIfAbsent(key, k -> newParam);
        oldValues.set(0, strValues);
        URI uri = URI.create(this.standardHttpRequestParam.getUrl());
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

        this.standardHttpRequestParam.setUrl(result.toString());
    }

    @Override
    public void addParameter(String key, String value) {
    }

    @Override
    public void removeParameter(String key) {
    }

    @Override
    public void setFormData(String key, String value, boolean isFile) {
    }

    @Override
    public void setHeader(String key, String value) {
        removeHeader(key);
        addHeader(key, value);
    }

    @Override
    public void addHeader(String key, String value) {
        standardHttpRequestParam.getHeaders().add(new KeyValue(key, value));
    }

    @Override
    public void removeHeader(String key) {
        standardHttpRequestParam.getHeaders().removeIf(keyValue -> StringUtils.isEqualsIgnoreCase(keyValue.getKey(), key));
    }

    @Override
    public byte[] getRequestBody() {
        return standardHttpRequestParam.getBody().contentConversion();
    }

    @Override
    public void setRequestBody(String body) {
        this.standardHttpRequestParam.setBody(new StringBody(body));
    }

    @Override
    public Map<String, List<String>> getParameterMap() {
        Map<String, List<String>> params = new HashMap<>();
        try {
            URI uri = URI.create(this.standardHttpRequestParam.getUrl());
            if (uri.getRawQuery() == null || "".equalsIgnoreCase(uri.getRawQuery())) {
                return params;
            }
            for (String param : uri.getQuery().split("&")) {
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = "";
                if (pair.length > 1) {
                    value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                }
                params.computeIfAbsent(key, k -> new ArrayList<>());
                List<String> oldValues = params.get(key);
                List<String> newValues = new ArrayList<>(oldValues);
                newValues.add(value);
                params.put(key, newValues);
            }
        } catch (Exception ignored) {
        }
        return params;
    }

    @Override
    public List<String> getParameterValues(String key) {
        return getParameterMap().getOrDefault(key, new ArrayList());
    }

    @Override
    public void setUrl(String newURL) {
        standardHttpRequestParam.setUrl(newURL);
    }

    @Override
    public String getUrl() {
        return URLDecoder.decode(standardHttpRequestParam.getUrl(), StandardCharsets.UTF_8);
    }

    @Override
    public String getHeader(String key) {
        for (KeyValue header : this.standardHttpRequestParam.getHeaders()) {
            if (StringUtils.isEqualsIgnoreCase(header.getKey(), key)) return header.getValue();
        }
        return null;
    }

    @Override
    public List<String> getHeaderKeys() {
        List<KeyValue> headers = this.standardHttpRequestParam.getHeaders();
        if (headers == null) {
            return new ArrayList<>();
        }
        return headers.stream().map(KeyValue::getKey).collect(Collectors.toList());
    }
}
