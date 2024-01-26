package com.cool.request.view.tool;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.ObjectMappingUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestParamCacheManager {
    private static void createIsNotExist() {
        if (!Files.exists(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING)) {
            try {
                Files.createDirectory(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING);
            } catch (IOException ignored) {
            }
        }
    }

    public static RequestCache setCache(String id, RequestCache requestCache) {
        createIsNotExist();
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING.toString(), id);
        try {
            Files.writeString(path, ObjectMappingUtils.getInstance().writeValueAsString(requestCache), StandardCharsets.UTF_8);
            return requestCache;
        } catch (IOException ignored) {
        }
        return null;
    }

    public static void removeCache(String id) {
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING.toString(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    public static RequestCache getCache(String id) {
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING.toString(), id);
        if (!Files.exists(path)) return null;
        try {
            return ObjectMappingUtils.getInstance().readValue(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), new TypeReference<RequestCache>() {
            });
        } catch (IOException ignored) {
        }
        return null;
    }

}
