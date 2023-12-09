package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;

public class ScriptLogPage extends JPanel {
    private final Log log;
    private final StringBuilder logBuffer = new StringBuilder();

    public ScriptLogPage(Project project) {
        this.setLayout(new BorderLayout());
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        this.log = new Log(project);
        jbTabs.addTab(new TabInfo(log).setText("Log"));
        add(jbTabs.getComponent(),BorderLayout.CENTER);
        project.getMessageBus().connect().subscribe(IdeaTopic.SCRIPT_LOG, new IdeaTopic.ScriptLogListener() {
            @Override
            public void log(String value) {
               SwingUtilities.invokeLater(() -> appendLog(value));
            }

            @Override
            public void clear() {
                SwingUtilities.invokeLater(() -> clearLog());
            }
        });
    }

    public void appendLog(String value) {
        logBuffer.append(value);
        log.setText(logBuffer.toString());
    }

    public void clearLog() {
        logBuffer.setLength(0);
        log.setText("");
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
