package com.cool.request.utils.param;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.springmvc.*;
import com.cool.request.utils.CollectionUtils;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.tool.RequestParamCacheManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class CacheParameterProvider implements HTTPParameterProvider {
    @Override
    public List<KeyValue> getHeader(Project project, Controller controller, RequestEnvironment environment) {
        RequestCache cache = RequestParamCacheManager.getCache(controller.getId());
        if (cache == null) return new ArrayList<>();
        return CollectionUtils.uniqueMerge(cache.getHeaders(), environment.getHeader());
    }

    @Override
    public List<KeyValue> getUrlParam(Project project, Controller controller, RequestEnvironment environment) {
        RequestCache cache = RequestParamCacheManager.getCache(controller.getId());
        if (cache == null) return new ArrayList<>();
        return CollectionUtils.uniqueMerge(cache.getUrlParams(), environment.getUrlParam());
    }

    @Override
    public Body getBody(Project project, Controller controller, RequestEnvironment environment) {
        RequestCache cache = RequestParamCacheManager.getCache(controller.getId());
        if (cache == null) return new EmptyBody();
        String requestBodyType = cache.getRequestBodyType();
        if (MediaTypes.APPLICATION_WWW_FORM.equalsIgnoreCase(requestBodyType)) {
            List<KeyValue> keyValues = UrlUtils.parseFormData(cache.getRequestBody());
            new FormUrlBody(CollectionUtils.merge(keyValues, environment.getFormUrlencoded()));
        }
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
}
