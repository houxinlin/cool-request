package com.cool.request.common.cache;

import com.cool.request.component.http.net.HTTPResponseBody;
import com.cool.request.lib.springmvc.RequestCache;

public interface CacheStorageService {
    public void storageRequestCache(String id, RequestCache requestCache);

    public void removeRequestCache(String id);

    public RequestCache getRequestCache(String id);


    void storageResponseCache(String requestId, HTTPResponseBody httpResponseBody);

    void removeResponseCache(String id);

    HTTPResponseBody getResponseCache(String requestId);


    void removeAllCache();

}
