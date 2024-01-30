package com.cool.request.component.http.net.request;

import com.cool.request.component.http.net.KeyValue;
import com.cool.request.utils.StringUtils;

import java.net.URI;
import java.net.URLEncoder;

public class HttpRequestParamUtils {
    public static String getFullUrl(StandardHttpRequestParam standardHttpRequestParam) {
        String url = standardHttpRequestParam.getUrl();
        for (KeyValue keyValue : standardHttpRequestParam.getUrlParam()) {
            url = addParameterToUrl(url, keyValue.getKey(), keyValue.getValue());
        }
        return url;
    }

    public static String addParameterToUrl(String baseUrl, String paramName, String paramValue) {
        try {
            URI uri = new URI(baseUrl);
            String query = uri.getQuery();
            if (query == null || "".equals(query)) {
                query = paramName + "=" + URLEncoder.encode(paramValue, "utf-8");
            } else {
                if (!baseUrl.endsWith("&")) query += "&";
                query += paramName + "=" + URLEncoder.encode(paramValue, "utf-8");
            }
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment()).toString();
        } catch (Exception e) {
        }
        return baseUrl;
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
