package com.cool.request.utils.param;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.components.http.Controller;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.lib.springmvc.*;
import com.cool.request.utils.CollectionUtils;
import com.cool.request.utils.ControllerUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.UrlUtils;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class CacheParameterProvider implements HTTPParameterProvider {
    @Override
    public List<KeyValue> getHeader(Project project, Controller controller, RequestEnvironment environment) {
        RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (cache == null) return new ArrayList<>();
        return CollectionUtils.merge(cache.getHeaders(), environment.getHeader());
    }

    @Override
    public List<KeyValue> getUrlParam(Project project, Controller controller, RequestEnvironment environment) {
        RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (cache == null) return new ArrayList<>();
        return CollectionUtils.merge(cache.getUrlParams(), environment.getUrlParam());
    }

    @Override
    public Body getBody(Project project, Controller controller, RequestEnvironment environment) {
        RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (cache == null) return new EmptyBody();

        String requestBodyType = cache.getRequestBodyType();
        //和全局form url合并
        if (MediaTypes.APPLICATION_WWW_FORM.equalsIgnoreCase(requestBodyType)) {
            List<KeyValue> keyValues = UrlUtils.parseFormData(cache.getRequestBody());
            new FormUrlBody(CollectionUtils.merge(keyValues, environment.getFormUrlencoded()));
        }
        //和全局for data合并
        if (MediaTypes.MULTIPART_FORM_DATA.equalsIgnoreCase(requestBodyType)) {
            return new FormBody(CollectionUtils.merge(cache.getFormDataInfos(), environment.getFormData()));
        }
        if (MediaTypes.APPLICATION_JSON.equalsIgnoreCase(requestBodyType)) {
            return new JSONBody(cache.getRequestBody());
        }
        if (MediaTypes.APPLICATION_XML.equalsIgnoreCase(requestBodyType)) {
            return new XMLBody(cache.getRequestBody());
        }
        if (MediaTypes.TEXT.equalsIgnoreCase(requestBodyType)) {
            return new StringBody(cache.getRequestBody());
        }
        if (MediaTypes.APPLICATION_STREAM.equalsIgnoreCase(requestBodyType)) {
            return new BinaryBody(cache.getRequestBody());
        }
        return new EmptyBody();
    }

    @Override
    public String getFullUrl(Project project, Controller controller, RequestEnvironment environment) {
        RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (cache != null) return cache.getUrl();
        if (!(environment instanceof EmptyEnvironment))
            return StringUtils.joinUrlPath(environment.getHostAddress(), controller.getContextPath(), controller.getUrl());

        return ControllerUtils.buildLocalhostUrl(controller);
    }

    @Override
    public HttpMethod getHttpMethod(Project project, Controller controller, RequestEnvironment environment) {
        RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (cache != null) return HttpMethod.parse(cache.getHttpMethod());
        return HttpMethod.GET;
    }
}
