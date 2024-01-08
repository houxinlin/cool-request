package com.hxl.plugin.springboot.invoke.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.bean.RequestMappingWrapper;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public static RequestCache setCache(String id, RequestCache requestCache) {
        createIsNotExist();
        Path path = Paths.get(Constant.CONFIG_CONTROLLER_SETTING.toString(), id);
        try {
            Files.write(path, ObjectMappingUtils.getInstance().writeValueAsString(requestCache).getBytes());
            return requestCache;
        } catch (IOException ignored) {
        }
        return null;
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
            return ObjectMappingUtils.getInstance().readValue(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), new TypeReference<RequestCache>() {
            });
        } catch (IOException ignored) {
        }
        return null;
    }

    public static RequestCache getCache(RequestMappingWrapper requestMappingModel) {
        return getCache(generatorCacheId(requestMappingModel.getController()));
    }

    public static RequestCache getCache(SpringMvcRequestMappingSpringInvokeEndpoint requestMappingModel) {
        return getCache(generatorCacheId(requestMappingModel));
    }

    public static String generatorCacheId(SpringMvcRequestMappingSpringInvokeEndpoint springMvcRequestMappingSpringInvokeEndpoint) {
        return springMvcRequestMappingSpringInvokeEndpoint.getId();
    }
}
