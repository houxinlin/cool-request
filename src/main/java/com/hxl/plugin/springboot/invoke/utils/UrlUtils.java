package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.net.KeyValue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class UrlUtils {
    public static String mapToUrlParams(List<KeyValue> params) {
        StringBuilder result = new StringBuilder();
        for (KeyValue param : params) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
        }
        return result.toString();
    }
    public static String mapToUrlParams(Map<String, Object> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return result.toString();
    }
}
