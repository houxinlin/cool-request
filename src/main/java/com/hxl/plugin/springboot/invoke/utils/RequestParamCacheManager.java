package com.hxl.plugin.springboot.invoke.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestParamCacheManager {
    private static void createIsNotExist() {
        if (!Files.exists(Constant.CONFIG_CONTROLLER_SETTING)) {
            try {
                Files.createDirectory(Constant.CONFIG_CONTROLLER_SETTING);
            } catch (IOException ignored) {
            }
        }
    }

    public static void setCache(String id, RequestCache requestCache) {
        createIsNotExist();
        Path path = Paths.get(Constant.CONFIG_CONTROLLER_SETTING.toString(), id);
        try {
            Files.write(path, ObjectMappingUtils.getInstance().writeValueAsString(requestCache).getBytes());
        } catch (IOException ignored) {
        }
    }
    public static void removeCache(String id) {
        Path path = Paths.get(Constant.CONFIG_CONTROLLER_SETTING.toString(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    public static RequestCache getCache(String id) {
        Path path = Paths.get(Constant.CONFIG_CONTROLLER_SETTING.toString(), id);
        if (!Files.exists(path)) return null;
        try {
            return ObjectMappingUtils.getInstance().readValue(new String(Files.readAllBytes(path)), new TypeReference<>() {
            });
        } catch (IOException ignored) {
        }
        return null;
    }
}
