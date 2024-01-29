package com.cool.request.utils.param;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.lib.springmvc.Body;
import com.intellij.openapi.project.Project;

import java.util.List;

public class GuessParameterProvider implements HTTPParameterProvider{
    @Override
    public List<KeyValue> getHeader(Project project, Controller controller, RequestEnvironment environment) {
        return null;
    }

    @Override
    public List<KeyValue> getUrlParam(Project project, Controller controller, RequestEnvironment environment) {
        return null;
    }

    @Override
    public Body getBody(Project project, Controller controller, RequestEnvironment environment) {
        return null;
    }
}
