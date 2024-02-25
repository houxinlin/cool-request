package com.cool.request.utils;

public class DataTypeUtils {
    public static String getDataType(Object object) {
        if (object == null) return "string";
        if ("true".equalsIgnoreCase(object.toString()) || "false".equalsIgnoreCase(object.toString())) return "boolean";
        if (object instanceof String) return "string";
        if (object instanceof Integer || object instanceof Long) return "integer";
        if (object instanceof Float || object instanceof Double) return "integer";
        if (object instanceof Short) return "integer";
        if (object instanceof Boolean) return "boolean";
        return "string";
    }

}
