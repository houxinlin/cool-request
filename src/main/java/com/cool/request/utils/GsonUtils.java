package com.cool.request.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GsonUtils {
    private static final Gson gson = new Gson();

    public static <T> T readValue(String value, Class<T> tClass) {
        try {
            return gson.fromJson(value, tClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toJsonString(Object obj) {
        return gson.toJson(obj);
    }

    public static String format(String source) {
        try {
            if (StringUtils.isEmpty(source)) return source;
            JsonElement jsonElement = new JsonParser().parse(source);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(jsonElement);
            return json;
        } catch (Exception e) {
            return source;
        }
    }
}
