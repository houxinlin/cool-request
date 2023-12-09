package com.hxl.plugin.springboot.invoke.view.page;

import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;

public class ScriptCodePage extends JPanel {
    private final TextEditPage requestTextEditPage;
    private final TextEditPage responseTextEditPage;

    public ScriptCodePage(Project project) {
        this.setLayout(new BorderLayout());
        requestTextEditPage = new TextEditPage(project);
        responseTextEditPage = new TextEditPage(project);
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        jbTabs.addTab(new TabInfo(requestTextEditPage).setText("Request"));
        jbTabs.addTab(new TabInfo(responseTextEditPage).setText("Response"));
        add(jbTabs.getComponent());
    }
    public String getRequestScriptText(){
        return this.requestTextEditPage.getText();
    }
    public String getResponseScriptText(){
        return this.responseTextEditPage.getText();
    }
    public void setScriptText(String requestScript, String responseScript) {
        this.requestTextEditPage.setText(requestScript);
        this.responseTextEditPage.setText(responseScript);
    }

}
