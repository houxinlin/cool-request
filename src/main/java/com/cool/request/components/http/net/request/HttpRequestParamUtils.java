package com.cool.request.components.http.net.request;

import com.cool.request.components.http.KeyValue;
import com.cool.request.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public class HttpRequestParamUtils {
    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    /**
     * 追加参数
     */
    public static String addParameterToUrl(String baseUrl, String paramName, String paramValue) {
        try {
            URL url = new URL(baseUrl);
            Map<String, List<String>> paramMap = splitQuery(url);
            List<String> values = paramMap.computeIfAbsent(paramName, s -> new ArrayList<>());
            values.add(paramValue == null ? "" : paramValue);

            StringBuilder query = new StringBuilder();
            for (String key : paramMap.keySet()) {
                for (String val : paramMap.getOrDefault(key, new ArrayList<>())) {
                    if (val == null) continue;
                    query.append(key).append("=").append(URLEncoder.encode(val, "utf-8")).append("&");
                }
            }
            StringBuilder result = new StringBuilder();
            result.append(url.getProtocol() + "://");
            result.append(url.getHost());
            if (url.getPort() != -1) {
                result.append(":").append(url.getPort());
            }
            if (url.getPath() != null) result.append(url.getPath());
            if (query != null) result.append("?").append(query);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseUrl;
    }


    public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(url.getQuery())) return new HashMap<>();
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }

    public static String getContentType(StandardHttpRequestParam standardHttpRequestParam, String def) {
        for (KeyValue header : standardHttpRequestParam.getHeaders()) {
            if (StringUtils.isEqualsIgnoreCase(header.getKey(), "content-type")) {
                if (!StringUtils.isEmpty(header.getValue())) return header.getValue();
            }
        }
        return def;
    }

    public static void setContentType(StandardHttpRequestParam standardHttpRequestParam, String contentType) {
        standardHttpRequestParam.getHeaders().removeIf(keyValue -> StringUtils.isEqualsIgnoreCase("content-type", keyValue.getKey()));
        standardHttpRequestParam.getHeaders().add(new KeyValue("Content-Type", contentType));
    }
}
