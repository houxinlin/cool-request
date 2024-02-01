package com.cool.request.utils;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
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
import com.cool.request.view.tool.RequestParamCacheManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

/**
 * CURL生成器
 */
public class CURLUtils {
    public static String generatorCurl(Project project, Controller controller) {
        RequestEnvironmentProvide requestEnvironmentProvide = ProviderManager.getProvider(RequestEnvironmentProvide.class, project);
        RequestEnvironment requestEnvironment = requestEnvironmentProvide.getSelectRequestEnvironment();

        RequestCache cache = RequestParamCacheManager.getCache(controller.getId());
        IRequestParamManager requestParamManager = ProviderManager.getProvider(IRequestParamManager.class, project);
        HTTPParameterProvider httpParameterProvider = getHttpParameterProvider(controller, requestParamManager, cache);
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
                            cUrl.data(StringUtils.joinSingleQuotation(new String(bytes)));
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
        HTTPParameterProvider httpParameterProvider = null;

        if (requestParamManager.isAvailable() &&
                requestParamManager.getCurrentController().getId().equalsIgnoreCase(controller.getId())) {
            httpParameterProvider = new PanelParameterProvider();
        }
        if (httpParameterProvider == null && cache != null) {
            httpParameterProvider = new CacheParameterProvider();
        }
        if (httpParameterProvider == null) {
            httpParameterProvider = new GuessParameterProvider();
        }
        return httpParameterProvider;
    }
}
