package com.cool.request.view.page;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;

public class ScriptLogPage extends JPanel {
    private final Log logPage;

    public ScriptLogPage(Project project) {
        this.setLayout(new BorderLayout());
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        this.logPage = new Log(project);
        jbTabs.addTab(new TabInfo(logPage).setText("Log"));
        add(jbTabs.getComponent(), BorderLayout.CENTER);
    }

    public void setLog(String value) {
        try {
            SwingUtilities.invokeLater(() -> logPage.setText(value));
        } catch (Exception ignored) {
        }
    }

    public void clearAllLog() {
        try {
            SwingUtilities.invokeLater(() -> logPage.setText(""));
        } catch (Exception ignored) {

        }
    }

    static class Log extends BasicEditPage {
        public Log(Project project) {
            super(project);
        }

        @Override
        public FileType getFileType() {
            return PlainTextFileType.INSTANCE;
        }
    }

}
