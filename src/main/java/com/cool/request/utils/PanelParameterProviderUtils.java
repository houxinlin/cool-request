package com.cool.request.utils;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.component.CoolRequestContext;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.param.CacheParameterProvider;
import com.cool.request.utils.param.GuessParameterProvider;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.project.Project;

public class PanelParameterProviderUtils {
    public static HTTPParameterProvider getPanelParameterProvider(Project project, Controller controller) {
        IRequestParamManager requestParamManager = CoolRequestContext.getInstance(project)
                .getMainBottomHTTPContainer()
                .getMainBottomHttpInvokeViewPanel()
                .getHttpRequestParamPanel();
        if (requestParamManager.isAvailable()) {
            if (StringUtils.isEqualsIgnoreCase(requestParamManager.getCurrentController().getId(), controller.getId())) {
                return new PanelParameterProvider(requestParamManager);
            }
        }
        RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (cache != null) {
            return new CacheParameterProvider();
        }
        return new GuessParameterProvider();
    }
}
