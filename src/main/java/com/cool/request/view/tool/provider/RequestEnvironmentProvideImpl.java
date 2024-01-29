package com.cool.request.view.tool.provider;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.lib.springmvc.FormUrlBody;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RequestEnvironmentProvideImpl implements RequestEnvironmentProvide {
    private Project project;

    public RequestEnvironmentProvideImpl(Project project) {
        this.project = project;
    }

    @Override
    public void applyEnvironmentParam(StandardHttpRequestParam standardHttpRequestParam) {
        RequestEnvironment selectRequestEnvironment = getSelectRequestEnvironment();

        for (KeyValue keyValue : selectRequestEnvironment.getUrlParam()) {
            standardHttpRequestParam.getUrlParam().add(keyValue);
        }

        for (KeyValue keyValue : selectRequestEnvironment.getHeader()) {
            standardHttpRequestParam.getHeaders().add(keyValue);
        }

        if (standardHttpRequestParam.getBody() instanceof FormUrlBody) {
            FormUrlBody formUrlBody = (FormUrlBody) standardHttpRequestParam.getBody();
            for (KeyValue keyValue : selectRequestEnvironment.getFormUrlencoded()) {
                formUrlBody.getData().add(keyValue);
            }
        }

        if (standardHttpRequestParam.getBody() instanceof FormBody) {
            FormBody formBody = (FormBody) standardHttpRequestParam.getBody();
            for (FormDataInfo keyValue : selectRequestEnvironment.getFormData()) {
                formBody.getData().add(keyValue);
            }
        }

    }

    @Override
    public @NotNull RequestEnvironment getSelectRequestEnvironment() {
        CoolRequestEnvironmentPersistentComponent.State state = project.getService(CoolRequestEnvironmentPersistentComponent.class).getState();
        if (StringUtils.isEmpty(state.getSelectId())) return new EmptyEnvironment();

        for (RequestEnvironment environment : state.getEnvironments()) {
            if (StringUtils.isEqualsIgnoreCase(state.getSelectId(), environment.getId())) return environment;
        }
        return new EmptyEnvironment();
    }

    @Override
    public String applyUrl(Controller controller) {
        if (getSelectRequestEnvironment() instanceof EmptyEnvironment) {
            return StringUtils.joinUrlPath("http://localhost:" + controller.getServerPort(),
                    StringUtils.getFullUrl(controller));
        }
        return StringUtils.joinUrlPath(getSelectRequestEnvironment().getHostAddress(), StringUtils.getFullUrl(controller));
    }
}
