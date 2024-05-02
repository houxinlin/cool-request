/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ComponentCacheManager.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.common.cache;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.TemporaryController;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;

/**
 * 所有组件缓存在这里处理
 */
@Service
public final class ComponentCacheManager {
    private Project project;

    public static ComponentCacheManager getInstance(Project project) {
        return project.getService(ComponentCacheManager.class);
    }

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
        this.project = project;
    }

    public void init() {
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        // 保存http响应缓存
        messageBusConnection.subscribe(CoolRequestIdeaTopic.HTTP_RESPONSE, (requestId, httpResponseBody, requestContext) -> {
            Controller controller = requestContext.getController();
            if (controller == null) return;
            if (controller instanceof TemporaryController) return;
            CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
            service.storageResponseCache(requestContext.getId(), httpResponseBody);
        });
    }
}
