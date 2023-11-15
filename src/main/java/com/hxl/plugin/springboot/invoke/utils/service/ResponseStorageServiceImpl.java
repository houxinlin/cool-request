package com.hxl.plugin.springboot.invoke.utils.service;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.intellij.openapi.components.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public final class ResponseStorageServiceImpl implements ResponseStorageService {
    @Override
    public void storage(String requestId, InvokeResponseModel invokeResponseModel) {
        Path path = Paths.get(Constant.CONFIG_RESPONSE_CACHE.toString(), requestId);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException ignored) {
            }
        }
        try {
            Files.write(path, ObjectMappingUtils.toJsonString(invokeResponseModel).getBytes());
        } catch (IOException ignored) {

        }
    }

    @Override
    public void deleteCache(String id) {
        Path path = Paths.get(Constant.CONFIG_RESPONSE_CACHE.toString(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    @Override
    public InvokeResponseModel load(String requestId) {
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
        } catch (IOException ignored) {

        }
        return defaultInvokeResponseModel;
    }
}
