package com.cool.request.cache;

import com.cool.request.IdeaTopic;
import com.cool.request.model.InvokeResponseModel;
import com.cool.request.utils.service.CacheStorageService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;

public class ComponentCacheManager {

    public ComponentCacheManager(Project project) {
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        // 保存http响应缓存
        messageBusConnection.subscribe(IdeaTopic.HTTP_RESPONSE, (IdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
            service.storageResponseCache(requestId, invokeResponseModel);
        });
    }

    public InvokeResponseModel getResponseCache(String requestId) {
        CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
        return service.loadResponseCache(requestId);
    }
}
