package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.net.RequestContext;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.view.main.MainBottomHTTPInvokeRequestParamManagerPanel;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ScriptLogPage extends JPanel {
    private final Log logPage;
    private final StringBuilder logBuffer = new StringBuilder();
    private final Project project;
    private final MainBottomHTTPInvokeRequestParamManagerPanel mainBottomHTTPInvokeRequestParamManagerPanel;

    public ScriptLogPage(Project project, MainBottomHTTPInvokeRequestParamManagerPanel mainBottomHTTPInvokeRequestParamManagerPanel) {
        this.mainBottomHTTPInvokeRequestParamManagerPanel = mainBottomHTTPInvokeRequestParamManagerPanel;
        this.project = project;
        this.setLayout(new BorderLayout());
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        this.logPage = new Log(project);
        jbTabs.addTab(new TabInfo(logPage).setText("Log"));
        add(jbTabs.getComponent(), BorderLayout.CENTER);
        project.getMessageBus().connect().subscribe(IdeaTopic.SCRIPT_LOG, new IdeaTopic.ScriptLogListener() {
            @Override
            public void log(String id, String value) {
                SwingUtilities.invokeLater(() -> appendLog(id, value));
            }

            @Override
            public void clear(String id) {
                SwingUtilities.invokeLater(() -> clearLog(id));
            }
        });
    }

    public void appendLog(String id, String value) {
        RequestContext requestContext = Objects.requireNonNull(this.project.getUserData(Constant.RequestContextManagerKey)).get(id);
        if (requestContext == null) return;
        String controllerId = this.mainBottomHTTPInvokeRequestParamManagerPanel.getCurrentRequestMappingModel().getController().getId();
        requestContext.appendLog(value);

        RequestCache cache = RequestParamCacheManager.getCache(id);
        if (cache != null) {
            cache.setScriptLog(requestContext.getLog());
            RequestParamCacheManager.setCache(id, cache);
        }
        if (id.equalsIgnoreCase(controllerId)) {
            logPage.setText(requestContext.getLog());
        }
    }

    public void setLog(String value) {
        logPage.setText(value);
    }

    public void clearLog(String id) {
        logBuffer.setLength(0);
        logPage.setText("");
    }

    public String getLogPage() {
        return logBuffer.toString();
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
