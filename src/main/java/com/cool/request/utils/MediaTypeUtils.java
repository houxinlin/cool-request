package com.cool.request.utils;

public class MediaTypeUtils {
    private static boolean contains(String src, String target) {
        if (StringUtils.isEmpty(src)) return false;
        if (target.isEmpty()) return false;
        return src.toLowerCase().contains(target.toLowerCase());
    }

    public static boolean isApplication(String src, String target) {
        return contains(src, "application/" + target);
    }

    public static boolean isFormData(String src) {
        return contains(src, "multipart/form-data");
    }

    public static boolean isXml(String src) {
        return isApplication(src, "xml");
    }

    public static boolean isJson(String src) {
        return isApplication(src, "json");
    }

    public static boolean isFormUrlencoded(String src) {
        return isApplication(src, "x-www-form-urlencoded");
    }

}
