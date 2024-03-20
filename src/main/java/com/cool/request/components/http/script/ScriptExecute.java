package com.cool.request.components.http.script;

import com.cool.request.components.http.net.RequestContext;
import com.intellij.openapi.project.Project;

public interface ScriptExecute {
    public boolean execRequest(Project project,
                               Request request) throws Exception;

    public void execResponse(Project project,
                             RequestContext requestContext,
                             Response response);
}
