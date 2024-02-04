package com.cool.request.common.cache;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.utils.FileUtils;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public final class CacheStorageServiceImpl implements CacheStorageService {
    private static final Logger LOG = Logger.getInstance(CacheStorageServiceImpl.class);

    @Override
    public void storageResponseCache(String requestId, InvokeResponseModel invokeResponseModel) {
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_RESPONSE_CACHE.toString(), requestId);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        try {
            LOG.info("storageResponseCache " + requestId);
            Files.write(path, GsonUtils.toJsonString(invokeResponseModel).getBytes());
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void deleteResponseCache(String id) {
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_RESPONSE_CACHE.toString(), id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public InvokeResponseModel loadResponseCache(String requestId) {
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_RESPONSE_CACHE.toString(), requestId);
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
    public void storageCustomCache(String type, String msg, Project project) {
        String fileName = StringUtils.calculateMD5(project.getName()) + "_" + type + ".cache";
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_DATA_CACHE.toString(), fileName);
        FileUtils.writeFile(path.toString(), msg);
    }

    @Override
    public String getCustomCache(String type, Project project) {
        String fileName = StringUtils.calculateMD5(project.getName()) + "_" + type + ".cache";
        Path path = Paths.get(CoolRequestConfigConstant.CONFIG_DATA_CACHE.toString(), fileName);
        if (Files.exists(path)) return FileUtils.readFile(path.toString());
        return null;
    }
}
