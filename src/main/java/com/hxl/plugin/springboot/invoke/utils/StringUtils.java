package com.hxl.plugin.springboot.invoke.utils;

public class StringUtils {
    public static boolean isEmpty( Object str) {
        return (str == null || "".equals(str));
    }
}
