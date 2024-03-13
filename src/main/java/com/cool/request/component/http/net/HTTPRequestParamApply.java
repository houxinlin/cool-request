package com.cool.request.component.http.net;

import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.intellij.openapi.project.Project;

public interface HTTPRequestParamApply {
    public void apply(Project project, StandardHttpRequestParam httpRequestParam);
}
