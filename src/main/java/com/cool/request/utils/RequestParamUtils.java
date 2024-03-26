package com.cool.request.utils;

import com.cool.request.components.http.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class RequestParamUtils {
    public static String getFirstValue(List<KeyValue> list, String key) {
        for (KeyValue keyValue : list) {
            if (StringUtils.isEquals(keyValue.getKey(), key)) return keyValue.getValue();
        }
        return null;
    }

    public static List<String> getValues(List<KeyValue> list, String key) {
        List<String> result = new ArrayList<>();
        for (KeyValue keyValue : list) {
            if (StringUtils.isEquals(keyValue.getKey(), key)) result.add(keyValue.getValue());
        }
        return result;
    }
}
