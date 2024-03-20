package com.cool.request.components.http.script;

import com.cool.request.components.http.net.RequestContext;
import com.cool.request.script.ILog;
import com.intellij.openapi.project.Project;

public class SimpleScriptExecute implements ScriptExecute {
    private String requestScript;
    private String responseScript;
    private ILog iScriptLog;

    public SimpleScriptExecute(String requestScript, String responseScript, ILog iScriptLog) {
        this.requestScript = requestScript;
        this.responseScript = responseScript;
        this.iScriptLog = iScriptLog;
    }

    @Override
    public boolean execRequest(Project project, Request request) throws Exception {
        JavaCodeEngine javaCodeEngine = new JavaCodeEngine(project);
        return javaCodeEngine.execRequest(request, requestScript);
    }

    @Override
    public void execResponse(Project project, RequestContext requestContext, Response response) {
        JavaCodeEngine javaCodeEngine = new JavaCodeEngine(project);
        javaCodeEngine.execResponse(response, responseScript, iScriptLog);
    }
}
