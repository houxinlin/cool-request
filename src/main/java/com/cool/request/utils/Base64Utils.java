package com.cool.request.utils;

import java.util.Base64;

public class Base64Utils {
    public static String encodeToString(byte[] data) {
        if (data == null) return "";
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decode(String str) {
        if (str == null) return new byte[0];
        return Base64.getDecoder().decode(str);
    }
}
