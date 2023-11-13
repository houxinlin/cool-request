package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.script.*;
import com.hxl.plugin.springboot.invoke.utils.ClassResourceUtils;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;


public class ScriptPage extends JPanel {
    private static final String REQUEST_CLASS = "com.hxl.plugin.springboot.invoke.script.RequestApi";
    private static final Logger LOG = Logger.getInstance(ScriptPage.class);
    private final TextEditPage requestTextEditPage;
    private final TextEditPage responseTextEditPage;

    public ScriptPage(Project project) {
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
