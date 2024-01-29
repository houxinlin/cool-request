package com.cool.request.utils.param;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.lib.springmvc.Body;
import com.cool.request.lib.springmvc.EmptyBody;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.lib.springmvc.FormUrlBody;
import com.cool.request.utils.CollectionUtils;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class PanelParameterProvider implements HTTPParameterProvider {
    @Override
    public List<KeyValue> getHeader(Project project, Controller controller, RequestEnvironment environment) {
        IRequestParamManager requestParamManager = ProviderManager.getProvider(IRequestParamManager.class, project);
        if (requestParamManager == null) return new ArrayList<>();
        return CollectionUtils.merge(requestParamManager.getHttpHeader(), environment.getHeader());
    }

    @Override
    public List<KeyValue> getUrlParam(Project project, Controller controller, RequestEnvironment environment) {
        IRequestParamManager requestParamManager = ProviderManager.getProvider(IRequestParamManager.class, project);
        if (requestParamManager == null) return new ArrayList<>();
        return CollectionUtils.merge(requestParamManager.getUrlParam(), environment.getUrlParam());
    }

    @Override
    public Body getBody(Project project, Controller controller, RequestEnvironment environment) {
        IRequestParamManager requestParamManager = ProviderManager.getProvider(IRequestParamManager.class, project);
        if (requestParamManager == null) return new EmptyBody();

        StandardHttpRequestParam standardHttpRequestParam = new StandardHttpRequestParam();
        requestParamManager.postApplyParam(standardHttpRequestParam);
        Body body = standardHttpRequestParam.getBody();

        if (body instanceof FormUrlBody) {
            return new FormUrlBody(CollectionUtils.merge(((FormUrlBody) body).getData(), environment.getFormUrlencoded()));
        }
        if (body instanceof FormBody) {
            return new FormBody(CollectionUtils.merge(((FormBody) body).getData(), environment.getFormData()));
        }
        return body;
    }
}
