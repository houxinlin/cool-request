package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;

public class ScriptCodePage extends JPanel {
    private final TextEditPage requestTextEditPage;
    private final TextEditPage responseTextEditPage;
    private TabInfo preTabInfo;
    private TabInfo postTabInfo;

    public ScriptCodePage(Project project) {
        this.setLayout(new BorderLayout());
        requestTextEditPage = new TextEditPage(project);
        responseTextEditPage = new TextEditPage(project);
        preTabInfo = new TabInfo(requestTextEditPage);
        postTabInfo = new TabInfo(responseTextEditPage);

        JBTabsImpl jbTabs = new JBTabsImpl(project);
        jbTabs.addTab(preTabInfo.setText("Request"));
        jbTabs.addTab(postTabInfo.setText("Response"));
        add(jbTabs.getComponent());

        project.getMessageBus().connect().subscribe(IdeaTopic.LANGUAGE_CHANGE, this::loadText);
        loadText();
    }

    private void loadText(){
        preTabInfo.setText(ResourceBundleUtils.getString("pre.script"));
        postTabInfo.setText(ResourceBundleUtils.getString("post.script"));

    }

    public String getRequestScriptText() {
        return this.requestTextEditPage.getText();
    }

    public String getResponseScriptText() {
        return this.responseTextEditPage.getText();
    }

    public void setScriptText(String requestScript, String responseScript) {
        this.requestTextEditPage.setText(requestScript);
        this.responseTextEditPage.setText(responseScript);
    }

}
