package com.cool.request.view.tool;

import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.application.ApplicationManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO: 2024/2/26 合并缓存管理
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
            Files.writeString(path, GsonUtils.toJsonString(requestCache), StandardCharsets.UTF_8);
            return requestCache;
        } catch (IOException ignored) {
        }
        return null;
    }

    public static void removeCache(String id) {
        if (StringUtils.isEmpty(id)) return;
        CacheStorageService cacheStorageService = ApplicationManager.getApplication().getService(CacheStorageService.class);
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING.toString(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
        cacheStorageService.deleteResponseCache(id);
    }

    public static void removeAllCache() {
        CacheStorageService cacheStorageService = ApplicationManager.getApplication().getService(CacheStorageService.class);
        cacheStorageService.removeAllCache();

        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING.toString());
        try {
            if (Files.notExists(path)) return;
            Files.list(path).forEach(path1 -> {
                try {
                    Files.deleteIfExists(path1);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {

        }

    }

    public static RequestCache getCache(String id) {
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING.toString(), id);
        if (!Files.exists(path)) return null;
        try {
            return GsonUtils.readValue(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), RequestCache.class);
        } catch (IOException ignored) {
        }
        return null;
    }

}
