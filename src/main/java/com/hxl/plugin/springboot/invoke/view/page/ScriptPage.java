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
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;


public class ScriptPage extends JBSplitter {
    private static final String REQUEST_CLASS = "com.hxl.plugin.springboot.invoke.script.RequestApi";
    private static final Logger LOG = Logger.getInstance(ScriptPage.class);
    private final ScriptCodePage scriptCodePage;
    private final ScriptLogPage scriptLogPage;
    public ScriptPage(Project project) {
        this.setOrientation(false);
        this.scriptCodePage =new ScriptCodePage(project);
        this.scriptLogPage= new ScriptLogPage(project);
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
}
