package com.hxl.plugin.springboot.invoke.tool.provider;

import com.hxl.plugin.springboot.invoke.bean.EmptyEnvironment;
import com.hxl.plugin.springboot.invoke.bean.RequestEnvironment;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.net.request.StandardHttpRequestParam;
import com.hxl.plugin.springboot.invoke.springmvc.FormBody;
import com.hxl.plugin.springboot.invoke.springmvc.FormUrlBody;
import com.hxl.plugin.springboot.invoke.state.CoolRequestEnvironmentPersistentComponent;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.main.RequestEnvironmentProvide;
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
