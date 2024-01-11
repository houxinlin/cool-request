package com.hxl.plugin.springboot.invoke.cache;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.utils.service.CacheStorageService;
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
