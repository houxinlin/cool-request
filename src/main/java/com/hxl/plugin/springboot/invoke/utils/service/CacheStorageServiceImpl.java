package com.hxl.plugin.springboot.invoke.utils.service;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public final class CacheStorageServiceImpl implements CacheStorageService {
    private static final Logger LOG = Logger.getInstance(CacheStorageServiceImpl.class);

    @Override
    public void storageResponseCache(String requestId, InvokeResponseModel invokeResponseModel) {
        Path path = Paths.get(Constant.CONFIG_RESPONSE_CACHE.toString(), requestId);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        try {
            LOG.info("storageResponseCache " + requestId);
            Files.write(path, ObjectMappingUtils.toJsonString(invokeResponseModel).getBytes());
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void deleteResponseCache(String id) {
        Path path = Paths.get(Constant.CONFIG_RESPONSE_CACHE.toString(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public InvokeResponseModel loadResponseCache(String requestId) {
        Path path = Paths.get(Constant.CONFIG_RESPONSE_CACHE.toString(), requestId);
        InvokeResponseModel defaultInvokeResponseModel = InvokeResponseModel.InvokeResponseModelBuilder.anInvokeResponseModel()
                .withData(new byte[0])
                .withId(requestId)
                .withType("response")
                .withHeader(new ArrayList<>())
                .build();
        if (!Files.exists(path))
            return defaultInvokeResponseModel;
        try {
            byte[] bytes = Files.readAllBytes(path);
            InvokeResponseModel invokeResponseModel = ObjectMappingUtils.readValue(new String(bytes), InvokeResponseModel.class);
            if (invokeResponseModel != null) return invokeResponseModel;
        } catch (IOException e) {
            LOG.error(e);
        }
        return defaultInvokeResponseModel;
    }
}
