package com.cool.request.utils;

import com.cool.request.state.SettingPersistentState;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtils {
    public static String getString(String key) {
        int languageValue = SettingPersistentState.getInstance().getState().languageValue;
        if (languageValue == -1) return getString(key, Locale.ENGLISH);
        if (languageValue == 0) return getString(key, Locale.ENGLISH);
        if (languageValue == 1) return getString(key, Locale.CHINESE);
        if (languageValue == 2) return getString(key, Locale.JAPANESE);
        if (languageValue == 3) return getString(key, Locale.KOREAN);
        return getString(key, Locale.getDefault());
    }

    private static String getString(String key, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }
}
