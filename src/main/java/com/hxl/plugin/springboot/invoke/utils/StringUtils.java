package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;

import java.net.URI;
import java.net.URISyntaxException;

public class StringUtils {
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static String headerNormalized(String headerName) {
        if (headerName == null || headerName.isEmpty()) {
            return headerName;
        }
        StringBuilder normalizedHeader = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : headerName.toLowerCase().toCharArray()) {
            if (c == '-') {
                normalizedHeader.append("-");
                capitalizeNext = true;
                continue;
            }
            normalizedHeader.append(capitalizeNext ? Character.toUpperCase(c) : c);
            if (capitalizeNext) {
                capitalizeNext = false;
            }
        }
        return normalizedHeader.toString();
    }

    public static String joinUrlPath(String... urlParts) {
        if (urlParts.length == 1) return urlParts[0];

        StringBuilder result = new StringBuilder();
        result.append(urlParts[0]);
        for (int i = 1; i < urlParts.length; i++) {
            String part = urlParts[i];
            if (StringUtils.isEmpty(part)) continue;
            if (!result.toString().endsWith("/") && !part.startsWith("/")) {
                result.append("/");
            }
            if (result.toString().endsWith("/") && part.startsWith("/")) {
                result.deleteCharAt(result.length() - 1);
            }
            result.append(part);
        }
        if (result.toString().startsWith("http")) return result.toString();
        if (result.toString().startsWith("/")) return result.toString();
        return "/" + result;

    }

    public static String getFullUrl(RequestMappingModel requestMappingModel) {
        String url = requestMappingModel.getController().getUrl();
        if (StringUtils.isEmpty(url)) return requestMappingModel.getContextPath();
        if (!url.startsWith("/"))url="/"+url;
        return joinUrlPath(requestMappingModel.getContextPath(), url);
    }

    public static String removeHostFromUrl(String url) {
        try {
            URI uri = new URI(url);
            URI newUri = new URI(
                    null,
                    null,
                    null,
                    0,
                    uri.getPath(),
                    uri.getQuery(),
                    uri.getFragment()
            );

            return newUri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

}
