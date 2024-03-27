/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * UrlUtils.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.utils;

import com.cool.request.components.http.KeyValue;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlUtils {
    public static String getUrlParam(String url) {
        try {
            return new URL(url).getQuery();
        } catch (MalformedURLException ignored) {

        }
        return "";
    }

    public static String addUrlParam(String url, String param) {
        if (StringUtils.isBlank(param)) return url;
        if (!url.endsWith("?")) url = url + "?";
        return url + param;
    }

    public static boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException ignored) {

        }
        return false;
    }

    public static int getPort(String str) throws MalformedURLException {
        URL url = new URL(str);
        int port = url.getPort();
        if (port == -1) return url.getDefaultPort();
        return port;

    }

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
            result.append(param.getKey());
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
