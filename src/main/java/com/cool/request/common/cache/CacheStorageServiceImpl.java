package com.cool.request.common.cache;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.FileUtils;
import com.cool.request.utils.GsonUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

@Service
public final class CacheStorageServiceImpl implements CacheStorageService {
    private static final Logger LOG = Logger.getInstance(CacheStorageServiceImpl.class);

    private static Path getRequestCacheFilePath(String id) {
        return Paths.get(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING.toString(), id);
    }

    private static Path getResponseCacheFilePath(String id) {
        return Paths.get(CoolRequestConfigConstant.CONFIG_RESPONSE_CACHE.toString(), id);
    }

    @Override
    public void removeRequestCache(String id) {
        try {
            FileUtils.deleteIfExists(getRequestCacheFilePath(id));
        } catch (IOException ignored) {

        }
    }

    @Override
    public RequestCache getRequestCache(String id) {
        if (!Files.exists(getRequestCacheFilePath(id))) return null;
        try {
            return GsonUtils.readValue(Files.readString(getRequestCacheFilePath(id)), RequestCache.class);
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public void storageRequestCache(String id, RequestCache requestCache) {
        try {
            FileUtils.writeString(getRequestCacheFilePath(id), GsonUtils.toJsonString(requestCache));
        } catch (IOException ignored) {
        }
    }

    @Override
    public void storageResponseCache(String requestId, InvokeResponseModel invokeResponseModel) {
        if (invokeResponseModel == null) return;
        Path responseCacheFilePath = getResponseCacheFilePath(requestId);
        try {
            FileUtils.writeString(responseCacheFilePath, GsonUtils.toJsonString(invokeResponseModel));
        } catch (IOException ignored) {

        }
    }

    @Override
    public void removeResponseCache(String id) {
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_RESPONSE_CACHE.toString(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {

        }

    }

    @Override
    public InvokeResponseModel getResponseCache(String requestId) {
        Path path = getResponseCacheFilePath(requestId);
        InvokeResponseModel defaultInvokeResponseModel = InvokeResponseModel.InvokeResponseModelBuilder.anInvokeResponseModel()
                .withData("")
                .withId(requestId)
                .withType("response")
                .withHeader(new ArrayList<>())
                .build();
        if (!Files.exists(path))
            return defaultInvokeResponseModel;
        try {
            byte[] bytes = Files.readAllBytes(path);
            InvokeResponseModel invokeResponseModel = GsonUtils.readValue(new String(bytes, StandardCharsets.UTF_8), InvokeResponseModel.class);
            if (invokeResponseModel != null) return invokeResponseModel;
        } catch (IOException e) {
            LOG.error(e);
        }
        return defaultInvokeResponseModel;
    }

    @Override
    public void removeAllCache() {
        Path responsePath = Paths.get(CoolRequestConfigConstant.CONFIG_RESPONSE_CACHE.toString());
        if (Files.notExists(responsePath)) return;
        try (Stream<Path> paths = Files.list(responsePath)) {
            paths.forEach(item -> {
                try {
                    Files.deleteIfExists(item);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }


        Path requestPath = Paths.get(CoolRequestConfigConstant.CONFIG_CONTROLLER_SETTING.toString());
        try (Stream<Path> paths = Files.list(requestPath)) {
            paths.forEach(item -> {
                try {
                    Files.deleteIfExists(item);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }

    }
}
