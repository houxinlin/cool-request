package com.cool.request.common.cache;

import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.lib.springmvc.RequestCache;

public interface CacheStorageService {
    public void storageRequestCache(String id, RequestCache requestCache);

    public void removeRequestCache(String id);

    public RequestCache getRequestCache(String id);


    void storageResponseCache(String requestId, InvokeResponseModel invokeResponseModel);

    void removeResponseCache(String id);

    InvokeResponseModel getResponseCache(String requestId);


    void removeAllCache();

}
