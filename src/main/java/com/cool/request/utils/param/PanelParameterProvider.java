package com.cool.request.utils.param;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.components.http.net.request.StandardHttpRequestParam;
import com.cool.request.lib.springmvc.Body;
import com.cool.request.lib.springmvc.EmptyBody;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.lib.springmvc.FormUrlBody;
import com.cool.request.utils.CollectionUtils;
import com.cool.request.utils.ControllerUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PanelParameterProvider implements HTTPParameterProvider {
    private final IRequestParamManager requestParamManager;

    public PanelParameterProvider(@NotNull IRequestParamManager requestParamManager) {
        this.requestParamManager = requestParamManager;
    }

    @Override
    public List<KeyValue> getHeader(Project project, Controller controller, RequestEnvironment environment) {
        if (!requestParamManager.isAvailable())
            return new ArrayList<>(environment.getHeader());
        return CollectionUtils.merge(requestParamManager.getHttpHeader(), environment.getHeader());
    }

    @Override
    public List<KeyValue> getUrlParam(Project project, Controller controller, RequestEnvironment environment) {
        if (!requestParamManager.isAvailable())
            return new ArrayList<>(environment.getUrlParam());
        return CollectionUtils.merge(requestParamManager.getUrlParam(), environment.getUrlParam());
    }

    @Override
    public List<KeyValue> getPathParam(Project project) {
        return requestParamManager.getPathParam();
    }

    @Override
    public Body getBody(Project project, Controller controller, RequestEnvironment environment) {
        if (!requestParamManager.isAvailable()) return new EmptyBody();

        StandardHttpRequestParam standardHttpRequestParam = new StandardHttpRequestParam();
        //从面板的参数映射器获取参数，主要提取post数据
        // TODO: 2024/1/30 面板参数器现在有多余的参数映射，用不到了，后续优化
        requestParamManager.postApplyParam(standardHttpRequestParam);
        Body body = standardHttpRequestParam.getBody();

        //处理form-url 和form-data，其他数据合并不了，直接返回
        if (body instanceof FormUrlBody) {
            return new FormUrlBody(CollectionUtils.merge(((FormUrlBody) body).getData(), environment.getFormUrlencoded()));
        }
        if (body instanceof FormBody) {
            return new FormBody(CollectionUtils.merge(((FormBody) body).getData(), environment.getFormData()));
        }
        //用户没填写body，但是请求体类型是form data，并且全局变量里面有form-data数据
        if (body == null && MediaTypes.MULTIPART_FORM_DATA.equals(requestParamManager.getRequestBodyType().getValue())) {
            if (!environment.getFormData().isEmpty()) {
                return new FormBody(environment.getFormData());
            }
        }
        return body;
    }

    @Override
    public String getFullUrl(Project project, Controller controller, RequestEnvironment environment) {
        if (requestParamManager.isAvailable()) return requestParamManager.getUrl();
        if (!(environment instanceof EmptyEnvironment))
            return StringUtils.joinUrlPath(environment.getHostAddress(), controller.getUrl());
        return ControllerUtils.buildLocalhostUrl(controller);
    }

    @Override
    public HttpMethod getHttpMethod(Project project, Controller controller, RequestEnvironment environment) {
        if (requestParamManager.isAvailable())
            return requestParamManager.getHttpMethod();
        return HttpMethod.GET; //默认返回GET
    }

}
