package com.cool.request.view.page;

import com.cool.request.view.main.HttpRequestParamPanel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;


public class ScriptPage extends JBSplitter {
    private static final String REQUEST_CLASS = "com.cool.request.script.RequestApi";
    private static final Logger LOG = Logger.getInstance(ScriptPage.class);
    private final ScriptCodePage scriptCodePage;
    private final ScriptLogPage scriptLogPage;
    public ScriptPage(Project project, HttpRequestParamPanel mainBottomHTTPInvokeRequestParamManagerPanel) {
        this.setOrientation(false);
        this.scriptCodePage =new ScriptCodePage(project);
        this.scriptLogPage= new ScriptLogPage(project,mainBottomHTTPInvokeRequestParamManagerPanel);
        this.setFirstComponent(this.scriptCodePage);
        this.setSecondComponent(this.scriptLogPage);
    }

    public String getRequestScriptText(){
        return this.scriptCodePage.getRequestScriptText();
    }
    public String getResponseScriptText(){
        return this.scriptCodePage.getResponseScriptText();
    }

    public void setScriptText(String requestScript, String responseScript) {
        this.scriptCodePage.setScriptText(requestScript,responseScript);
    }

    public void setLog(String id,String scriptLog) {
        scriptLogPage.clearLog(id);
        scriptLogPage.setLog(scriptLog);
    }
}
