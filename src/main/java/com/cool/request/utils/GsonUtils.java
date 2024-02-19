package com.cool.request.utils;


import com.google.gson.*;
import com.intellij.util.Base64;

import java.lang.reflect.Type;

public class GsonUtils {
    private static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class,
            new ByteArrayToBase64TypeAdapter()).create();

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
            return gson.toJson(jsonElement);
        } catch (Exception e) {
            return source;
        }
    }

    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.decode(json.getAsString());
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encode(src));
        }
    }
}
