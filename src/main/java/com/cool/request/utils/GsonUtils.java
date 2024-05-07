/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * GsonUtils.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.utils;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonUtils {
    private static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class,
            new ByteArrayToBase64TypeAdapter()).create();

    public static <T> T readValue(String value, Class<T> tClass) {
        try {
            return gson.fromJson(value, tClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> toMap(String json) {
        if (StringUtils.isEmpty(json)) return new HashMap<>();
        try {
            java.lang.reflect.Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            return gson.fromJson(json, type);
        } catch (Exception ignored) {
        }
        return new HashMap<>();
    }

    public static List<Map<String, Object>> toListMap(String json) {
        if (StringUtils.isEmpty(json)) return new ArrayList<>();
        try {
            Type listType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            return gson.fromJson(json, listType);
        } catch (Exception ignored) {
        }
        return new ArrayList<>();
    }

    public static String toJsonString(Object obj) {
        return gson.toJson(obj);
    }

    public static String format(String source) {
        try {
            if (StringUtils.isEmpty(source)) return source;
            JsonElement jsonElement = new JsonParser().parse(source);
            Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();
            return gson.toJson(jsonElement);
        } catch (Exception e) {
            return source;
        }
    }

    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64Utils.decode(json.getAsString());
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64Utils.encodeToString(src));
        }
    }

    public static boolean isArray(String json) {
        if (StringUtils.isEmpty(json)) return false;
        JsonParser parser = new JsonParser();
        try {
            JsonElement element = parser.parse(json);
            if (element.isJsonArray()) return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isObject(String json) {
        if (StringUtils.isEmpty(json)) return false;
        JsonParser parser = new JsonParser();
        try {
            JsonElement element = parser.parse(json);
            if (element.isJsonObject()) return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
