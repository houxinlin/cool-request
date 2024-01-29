package com.cool.request.utils.param;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.lib.springmvc.Body;
import com.intellij.openapi.project.Project;

import java.util.List;

public interface HTTPParameterProvider {
    /**
     * 获取请求头
     */
    public List<KeyValue> getHeader(Project project, Controller controller, RequestEnvironment environment);

    /**
     * 获取url参数
     */
    public List<KeyValue> getUrlParam(Project project, Controller controller, RequestEnvironment environment);

    /**
     * 获取请求体
     */
    public Body getBody(Project project, Controller controller, RequestEnvironment environment);

}
