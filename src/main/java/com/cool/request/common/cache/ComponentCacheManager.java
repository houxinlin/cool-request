package com.cool.request.common.cache;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.TemporaryController;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.component.http.net.RequestContextManager;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;

/**
 * 所有组件缓存在这里处理
 */
public class ComponentCacheManager {

    public static void storageRequestCache(String id, RequestCache requestCache) {
        ApplicationManager.getApplication().getService(CacheStorageService.class).storageRequestCache(id, requestCache);
    }

    public static void removeCache(String id) {
        if (StringUtils.isEmpty(id)) return;
        CacheStorageService cacheStorageService = ApplicationManager.getApplication().getService(CacheStorageService.class);
        cacheStorageService.removeResponseCache(id);
        cacheStorageService.removeRequestCache(id);
    }

    public static void removeAllCache() {
        CacheStorageService cacheStorageService = ApplicationManager.getApplication().getService(CacheStorageService.class);
        cacheStorageService.removeAllCache();
    }

    public static RequestCache getRequestParamCache(String id) {
        CacheStorageService cacheStorageService = ApplicationManager.getApplication().getService(CacheStorageService.class);
        return cacheStorageService.getRequestCache(id);
    }

    public ComponentCacheManager(Project project) {
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        // 保存http响应缓存
        messageBusConnection.subscribe(CoolRequestIdeaTopic.HTTP_RESPONSE, (CoolRequestIdeaTopic.HttpResponseEventListener) (requestId, httpResponseBody, requestContext) -> {
            RequestContextManager requestContextManager = project.getUserData(CoolRequestConfigConstant.RequestContextManagerKey);
            if (requestContextManager == null) return;
            Controller controller = requestContextManager.getCurrentController(requestId);
            if (controller instanceof TemporaryController) return;
            CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
            service.storageResponseCache(requestId, httpResponseBody);
        });
    }
}
