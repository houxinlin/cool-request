package com.hxl.plugin.springboot.invoke.utils;

public class StringUtils {
    public static boolean isEmpty( Object str) {
        return (str == null || "".equals(str));
    }

    public static String headerNormalized(String headerName) {
        if (headerName == null || headerName.isEmpty()) {
            return headerName;
        }
        StringBuilder normalizedHeader = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : headerName.toLowerCase().toCharArray()) {
            if (c=='-'){
                normalizedHeader.append("-");
                capitalizeNext=true;
                continue;
            }
            normalizedHeader.append(capitalizeNext?Character.toUpperCase(c):c);
            if (capitalizeNext){
                capitalizeNext = false;
            }
        }
        return normalizedHeader.toString();
    }

}
