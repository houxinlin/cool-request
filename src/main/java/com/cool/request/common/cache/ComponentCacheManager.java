package com.cool.request.common.cache;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.TemporaryController;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.component.http.net.RequestContextManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;

/**
 * 所有组件缓存在这里处理
 */
public class ComponentCacheManager {

    public ComponentCacheManager(Project project) {
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        // 保存http响应缓存
        messageBusConnection.subscribe(CoolRequestIdeaTopic.HTTP_RESPONSE, (CoolRequestIdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            RequestContextManager requestContextManager = project.getUserData(CoolRequestConfigConstant.RequestContextManagerKey);
            Controller controller = requestContextManager.getCurrentController(requestId);
            if (controller instanceof TemporaryController) return;
            CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
            service.storageResponseCache(requestId, invokeResponseModel);
        });
    }

}
