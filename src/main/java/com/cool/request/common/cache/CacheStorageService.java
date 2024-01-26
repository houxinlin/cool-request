package com.cool.request.common.cache;

import com.cool.request.common.model.InvokeResponseModel;
import com.intellij.openapi.project.Project;

public interface CacheStorageService {
    void storageResponseCache(String requestId, InvokeResponseModel invokeResponseModel);

    InvokeResponseModel loadResponseCache(String requestId);

    void deleteResponseCache(String id);

    void storageCustomCache(String type, String msg, Project project);
    String getCustomCache(String type, Project project);
}
