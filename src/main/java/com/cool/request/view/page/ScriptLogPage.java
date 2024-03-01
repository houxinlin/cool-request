package com.cool.request.view.page;

import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.component.http.net.RequestContext;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.IRequestParamManager;
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
    private final IRequestParamManager requestParamManager;

    public ScriptLogPage(Project project, IRequestParamManager requestParamManager) {
        this.requestParamManager = requestParamManager;
        this.project = project;
        this.setLayout(new BorderLayout());
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        this.logPage = new Log(project);
        jbTabs.addTab(new TabInfo(logPage).setText("Log"));
        add(jbTabs.getComponent(), BorderLayout.CENTER);
        project.getMessageBus().connect().subscribe(CoolRequestIdeaTopic.SCRIPT_LOG, new CoolRequestIdeaTopic.ScriptLogListener() {
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
        RequestContext requestContext = Objects.requireNonNull(this.project.getUserData(CoolRequestConfigConstant.RequestContextManagerKey)).get(id);
        if (requestContext == null) return;
        String controllerId = this.requestParamManager.getCurrentController().getId();
        requestContext.appendLog(value);

        RequestCache cache = ComponentCacheManager.getRequestParamCache(id);
        if (cache != null) {
            cache.setScriptLog(requestContext.getLog());
            ComponentCacheManager.storageRequestCache(id, cache);
        }
        if (id.equalsIgnoreCase(controllerId)) {
            logPage.setText(requestContext.getLog());
        }
    }

    public void setLog(String value) {
        logPage.setText(value);
    }

    public void clearLog(String id) {
        RequestContext requestContext = Objects.requireNonNull(this.project.getUserData(CoolRequestConfigConstant.RequestContextManagerKey)).get(id);
        if (requestContext == null) return;
        String controllerId = this.requestParamManager.getCurrentController().getId();
        if (StringUtils.isEqualsIgnoreCase(controllerId,id)){
            requestContext.clear();
            logBuffer.setLength(0);
            logPage.setText("");
        }

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
