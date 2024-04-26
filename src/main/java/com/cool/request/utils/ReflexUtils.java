package com.cool.request.utils;

import java.lang.reflect.Method;

public class ReflexUtils {
    public static void invokeMethod(Object obj, String name, Object[] parameter, Class<?>... parameterTypes) {
        try {
            Method method = obj.getClass().getMethod(name, parameterTypes);
            method.invoke(obj, parameter);
        } catch (Exception ignored) {
        }
    }
}
