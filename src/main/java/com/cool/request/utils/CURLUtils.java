package com.cool.request.utils;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.component.CoolRequestContext;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.HttpMethod;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.request.HttpRequestParamUtils;
import com.cool.request.lib.curl.CUrl;
import com.cool.request.lib.springmvc.*;
import com.cool.request.utils.param.CacheParameterProvider;
import com.cool.request.utils.param.GuessParameterProvider;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * CURL生成器
 */
public class CURLUtils {
    public static String generatorCurl(Project project, Controller controller, HTTPParameterProvider httpParameterProvider) {
        RequestEnvironmentProvide requestEnvironmentProvide = ProviderManager.getProvider(RequestEnvironmentProvide.class, project);
        RequestEnvironment requestEnvironment = requestEnvironmentProvide.getSelectRequestEnvironment();

        RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (httpParameterProvider == null) {
            //从JTree中生成，httpParameterProvider是null，自行推断，但是requestParamManager保证不为空
            IRequestParamManager requestParamManager = CoolRequestContext.getInstance(project).getMainRequestParamManager();
            httpParameterProvider = getHttpParameterProvider(controller, requestParamManager, cache);
        }
        try {
            HttpMethod httpMethod = httpParameterProvider
                    .getHttpMethod(project, controller, requestEnvironment);

            CUrl cUrl = new CUrl();

            String url = httpParameterProvider.getUrl(project, controller, requestEnvironment);
            List<KeyValue> urlParam = httpParameterProvider.getUrlParam(project, controller, requestEnvironment);

            for (KeyValue keyValue : urlParam) {
                url = HttpRequestParamUtils.addParameterToUrl(url, keyValue.getKey(), keyValue.getValue());
            }

            for (KeyValue keyValue : httpParameterProvider.getHeader(project, controller, requestEnvironment)) {
                cUrl.header(keyValue.getKey() + ":" + keyValue.getValue());
            }
            if (!httpMethod.equals(HttpMethod.GET)) {
                Body body = httpParameterProvider.getBody(project, controller, requestEnvironment);
                if (body != null && !(body instanceof EmptyBody)) {
                    cUrl.header("Content-Type:" + body.getMediaType());
                    if (body instanceof FormBody) {
                        for (FormDataInfo datum : ((FormBody) body).getData()) {
                            cUrl.form(datum.getName(), datum.getValue(), "file".equals(datum.getType()));
                        }
                    } else if (body instanceof BinaryBody) {
                        cUrl.data("@" + ((BinaryBody) body).getSelectFile(), true);
                    } else {
                        byte[] bytes = body.contentConversion();
                        if (bytes != null) {
                            cUrl.data(StringUtils.joinSingleQuotation(new String(bytes, StandardCharsets.UTF_8)));
                        }
                    }
                }
            }
            cUrl.method(httpMethod.toString().toUpperCase()).url(url);
            return cUrl.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 参数提供器，优先级为面板参数、缓存参数、推测参数
     */
    @NotNull
    private static HTTPParameterProvider getHttpParameterProvider(Controller controller, IRequestParamManager requestParamManager, RequestCache cache) {
        if (requestParamManager.isAvailable() &&
                requestParamManager.getCurrentController().getId().equalsIgnoreCase(controller.getId())) {
            return new PanelParameterProvider(requestParamManager);
        }
        if (cache != null) {
            return new CacheParameterProvider();
        }
        return new GuessParameterProvider();
    }
}
