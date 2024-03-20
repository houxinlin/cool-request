package com.cool.request.common.cache;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.components.http.net.HTTPResponseBody;
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
    public void storageResponseCache(String requestId, HTTPResponseBody httpResponseBody) {
        if (httpResponseBody == null) return;
        Path responseCacheFilePath = getResponseCacheFilePath(requestId);
        try {
            FileUtils.writeString(responseCacheFilePath, GsonUtils.toJsonString(httpResponseBody));
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
    public HTTPResponseBody getResponseCache(String requestId) {
        Path path = getResponseCacheFilePath(requestId);
        HTTPResponseBody defaultHTTPResponseBody = HTTPResponseBody.InvokeResponseModelBuilder.anInvokeResponseModel()
                .withData("")
                .withId(requestId)
                .withHeader(new ArrayList<>())
                .build();
        if (!Files.exists(path))
            return defaultHTTPResponseBody;
        try {
            byte[] bytes = Files.readAllBytes(path);
            HTTPResponseBody httpResponseBody = GsonUtils.readValue(new String(bytes, StandardCharsets.UTF_8), HTTPResponseBody.class);
            if (httpResponseBody != null) return httpResponseBody;
        } catch (IOException e) {
            LOG.error(e);
        }
        return defaultHTTPResponseBody;
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
