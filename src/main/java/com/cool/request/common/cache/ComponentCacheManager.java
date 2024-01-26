package com.cool.request.common.cache;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.InvokeResponseModel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;

public class ComponentCacheManager {

    public ComponentCacheManager(Project project) {
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        // 保存http响应缓存
        messageBusConnection.subscribe(CoolRequestIdeaTopic.HTTP_RESPONSE, (CoolRequestIdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
            service.storageResponseCache(requestId, invokeResponseModel);
        });
    }

    public InvokeResponseModel getResponseCache(String requestId) {
        CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
        return service.loadResponseCache(requestId);
    }
}
