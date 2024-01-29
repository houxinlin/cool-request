package com.cool.request.utils;

import com.cool.request.component.http.net.KeyValue;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlUtils {
    public static List<KeyValue> parseFormData(String formData) {
        if (StringUtils.isEmpty(formData)) return new ArrayList<>();
        List<KeyValue> result = new ArrayList<>();
        String[] pairs = formData.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                result.add(new KeyValue(key, value));
            }
        }
        return result;
    }

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

    public static String mapToUrlParams(Map<String, List<String>> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            if (entry.getValue() == null) continue;
            for (String value : entry.getValue()) {
                if (result.length() > 0) {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                result.append("=");
                result.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        }
        return result.toString();
    }
}
