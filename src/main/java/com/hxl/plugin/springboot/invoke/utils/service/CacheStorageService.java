package com.hxl.plugin.springboot.invoke.utils.service;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;

public interface CacheStorageService {
    void storageResponseCache(String requestId, InvokeResponseModel invokeResponseModel);
    InvokeResponseModel loadResponseCache(String requestId);
    void deleteResponseCache(String id);

}
