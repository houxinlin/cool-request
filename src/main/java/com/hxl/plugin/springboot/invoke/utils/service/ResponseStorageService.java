package com.hxl.plugin.springboot.invoke.utils.service;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;

public interface ResponseStorageService {

    void storage(String requestId, InvokeResponseModel invokeResponseModel);

    InvokeResponseModel load(String requestId);

    void deleteCache(String id);

}
