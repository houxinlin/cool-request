package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPContainer extends JPanel implements CommunicationListener {
    private final MainBottomHTTPInvokeViewPanel mainBottomHttpInvokeViewPanel;
    private final MainBottomHTTPResponseView mainBottomHTTPResponseView;

    public MainBottomHTTPContainer(Project project, CoolIdeaPluginWindowView coolIdeaPluginWindowView) {
        this.mainBottomHttpInvokeViewPanel = new MainBottomHTTPInvokeViewPanel(project, coolIdeaPluginWindowView);
        this.mainBottomHTTPResponseView = new MainBottomHTTPResponseView(project);

        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(this.mainBottomHttpInvokeViewPanel);
        jbSplitter.setSecondComponent(mainBottomHTTPResponseView);
        this.setLayout(new BorderLayout());
        this.add(jbSplitter, BorderLayout.CENTER);

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(IdeaTopic.DELETE_ALL_DATA, (IdeaTopic.DeleteAllDataEventListener) () -> {
            mainBottomHttpInvokeViewPanel.clearRequestParam();
//            mainBottomHttpInvokeViewPanel.controllerChooseEvent(null);
//            mainBottomHttpInvokeViewPanel.scheduledChooseEvent(null, -1);
        });
        connection.subscribe(IdeaTopic.CLEAR_REQUEST_CACHE, new IdeaTopic.ClearRequestCacheEventListener() {
            @Override
            public void onClearEvent(String id) {
                Controller controller = MainBottomHTTPContainer.this.mainBottomHttpInvokeViewPanel.getController();
                if (controller == null) return;
                if (controller.getId().equalsIgnoreCase(id)) {
                }
            }
        });
    }
}
