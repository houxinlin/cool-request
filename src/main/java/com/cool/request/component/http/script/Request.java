package com.cool.request.component.http.script;

import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.request.HttpRequestParamUtils;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.lib.springmvc.Body;
import com.cool.request.lib.springmvc.ByteBody;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.lib.springmvc.StringBody;
import com.cool.request.script.HTTPRequest;
import com.cool.request.script.ILog;
import com.cool.request.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request implements HTTPRequest {
    private final StandardHttpRequestParam standardHttpRequestParam;
    private final SimpleScriptLog scriptSimpleLog;

    public Request(StandardHttpRequestParam standardHttpRequestParam, SimpleScriptLog scriptSimpleLog) {
        this.standardHttpRequestParam = standardHttpRequestParam;
        this.scriptSimpleLog = scriptSimpleLog;
    }

    public ILog getScriptSimpleLog() {
        return scriptSimpleLog;
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
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public void setParameter(String key, String newValues) {
        if (StringUtils.isEmpty(key)) return;
        if (newValues == null) return;
        Map<String, List<String>> queryParamsMap = getParameterMap();
        queryParamsMap.remove(key);
        List<String> newParam = new ArrayList<>();
        newParam.add(newValues);
        queryParamsMap.putIfAbsent(key, newParam);
        URI uri = URI.create(standardHttpRequestParam.getUrl());
        StringBuilder query = new StringBuilder();
        for (String paramKey : queryParamsMap.keySet()) {
            for (String s : queryParamsMap.get(paramKey)) {
                query.append(paramKey).append("=").append(s).append("&");
            }
        }
        try {
            this.standardHttpRequestParam.setUrl(new URI(uri.getScheme(),
                    uri.getUserInfo(),
                    uri.getHost(),
                    uri.getPort(),
                    uri.getPath(),
                    query.toString(),
                    uri.getFragment()).toString());

        } catch (URISyntaxException ignored) {
        }
    }

    @Override
    public void addParameter(String key, String value) {
        try {
            URI uri = URI.create(standardHttpRequestParam.getUrl());
            Map<String, List<String>> queryParamsMap = getParameterMap();
            queryParamsMap.computeIfAbsent(key, s -> new ArrayList<>()).add(value);
            StringBuilder query = new StringBuilder();
            for (String paramKey : queryParamsMap.keySet()) {
                for (String s : queryParamsMap.get(paramKey)) {
                    query.append(paramKey).append("=").append(s).append("&");
                }
            }
            standardHttpRequestParam.setUrl(new URI(uri.getScheme(),
                    uri.getUserInfo(),
                    uri.getHost(),
                    uri.getPort(),
                    uri.getPath(),
                    query.toString(),
                    uri.getFragment()).toString());
        } catch (URISyntaxException ignored) {

        }
    }

    @Override
    public void removeParameter(String key) {
        try {
            URI uri = URI.create(standardHttpRequestParam.getUrl());
            Map<String, List<String>> queryParamsMap = getParameterMap();
            queryParamsMap.remove(key);
            StringBuilder query = new StringBuilder();
            for (String paramKey : queryParamsMap.keySet()) {
                for (String s : queryParamsMap.get(paramKey)) {
                    query.append(paramKey).append("=").append(s).append("&");
                }
            }
            standardHttpRequestParam.setUrl(new URI(uri.getScheme(),
                    uri.getUserInfo(),
                    uri.getHost(),
                    uri.getPort(),
                    uri.getPath(),
                    query.toString(),
                    uri.getFragment()).toString());
        } catch (URISyntaxException ignored) {

        }
    }

    @Override
    public void setFormData(String key, String value, boolean isFile) {
        Body body = standardHttpRequestParam.getBody();
        if (body instanceof FormBody) {
            ((FormBody) body).getData().removeIf(formDataInfo -> StringUtils.isEqualsIgnoreCase(formDataInfo.getName(), key));
            ((FormBody) body).getData().add(new FormDataInfo(key, value, isFile ? "file" : "text"));
        }
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
    public void setRequestBody(byte[] bytes) {
        this.standardHttpRequestParam.setBody(new ByteBody(bytes));
    }

    @Override
    public void setRequestBody(InputStream inputStream) {
        if (inputStream != null) {
            try {
                byte[] bytes = inputStream.readAllBytes();
                this.standardHttpRequestParam.setBody(new ByteBody(bytes));
            } catch (IOException e) {
                scriptSimpleLog.println(e.getMessage());
            }
        }
    }

    @Override
    public Map<String, List<String>> getParameterMap() {
        try {
            return HttpRequestParamUtils.splitQuery(new URL(this.standardHttpRequestParam.getUrl()));
        } catch (Exception e) {
            e.printStackTrace(this.scriptSimpleLog);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getParameterValues(String key) {
        return getParameterMap().getOrDefault(key, new ArrayList<>());
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
