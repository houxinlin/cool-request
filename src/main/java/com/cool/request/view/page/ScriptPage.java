package com.cool.request.view.page;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;


public class ScriptPage extends JBSplitter {
    private final ScriptCodePage scriptCodePage;
    private final ScriptLogPage scriptLogPage;

    public ScriptPage(Project project) {
        this.setOrientation(false);
        this.scriptCodePage = new ScriptCodePage(project);
        this.scriptLogPage = new ScriptLogPage(project);
        this.setFirstComponent(this.scriptCodePage);
        this.setSecondComponent(this.scriptLogPage);
    }

    public ScriptLogPage getScriptLogPage() {
        return scriptLogPage;
    }

    public String getRequestScriptText() {
        return this.scriptCodePage.getRequestScriptText();
    }

    public String getResponseScriptText() {
        return this.scriptCodePage.getResponseScriptText();
    }

    public void setScriptText(String requestScript, String responseScript) {
        this.scriptCodePage.setScriptText(requestScript, responseScript);
    }

    public void setLog(String id, String scriptLog) {
        scriptLogPage.clearAllLog();
        scriptLogPage.setLog(scriptLog);
    }
}
