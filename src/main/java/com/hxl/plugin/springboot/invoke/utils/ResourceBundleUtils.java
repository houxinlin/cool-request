package com.hxl.plugin.springboot.invoke.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtils {
    public static String getString(String key){
        Locale locale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }
}
