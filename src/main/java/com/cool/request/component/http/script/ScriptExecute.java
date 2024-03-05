package com.cool.request.component.http.script;

import com.cool.request.component.http.net.RequestContext;
import com.intellij.openapi.project.Project;

public interface ScriptExecute {
    public boolean execRequest(Project project,
                               Request request) throws Exception;

    public void execResponse(Project project,
                             RequestContext requestContext,
                             Response response);
}
