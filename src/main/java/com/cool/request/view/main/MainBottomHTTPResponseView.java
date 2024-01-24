package com.cool.request.view.main;

import com.cool.request.Constant;
import com.cool.request.IdeaTopic;
import com.cool.request.bean.components.controller.Controller;
import com.cool.request.model.InvokeResponseModel;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.page.HTTPResponseHeaderView;
import com.cool.request.view.page.HTTPResponseView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPResponseView extends JPanel {
    private final Project project;
    private HTTPResponseView httpResponseView;
    private HTTPResponseHeaderView httpResponseHeaderView;
    private TabInfo headerView;
    private TabInfo responseTabInfo;
    private Controller controller;

    public MainBottomHTTPResponseView(final Project project) {
        this.project = project;
        initUI();
        MessageBusConnection connect = project.getMessageBus().connect();
        loadText();
        connect.subscribe(IdeaTopic.SCHEDULED_CHOOSE_EVENT, (IdeaTopic.ScheduledChooseEventListener) (springScheduledSpringInvokeEndpoint) -> {
            httpResponseHeaderView.setText("");
            httpResponseView.reset();
        });

        connect.subscribe(IdeaTopic.CONTROLLER_CHOOSE_EVENT, (IdeaTopic.ControllerChooseEventListener) controller -> {
            MainBottomHTTPResponseView.this.controller = controller;
            InvokeResponseModel responseCache = project.getUserData(Constant.ComponentCacheManagerKey).getResponseCache(controller.getId());
            if (responseCache != null) {
                onHttpResponseEvent(controller.getId(), responseCache);
            }
        });

        connect.subscribe(IdeaTopic.HTTP_RESPONSE, (IdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            if (controller.getId().equalsIgnoreCase(requestId)) {
                onHttpResponseEvent(requestId, invokeResponseModel);
            }
        });
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(IdeaTopic.COOL_REQUEST_SETTING_CHANGE,
                (IdeaTopic.BaseListener) () -> loadText());

    }

    private void loadText() {
        headerView.setText(ResourceBundleUtils.getString("header"));
        responseTabInfo.setText(ResourceBundleUtils.getString("response"));
    }

    private void initUI() {
        JBTabsImpl tabs = new JBTabsImpl(project);
        httpResponseHeaderView = new HTTPResponseHeaderView(project);
        headerView = new TabInfo(httpResponseHeaderView);
        headerView.setText("Header");
        tabs.addTab(headerView);

        httpResponseView = new HTTPResponseView(project);
        responseTabInfo = new TabInfo(httpResponseView);
        responseTabInfo.setText("Response");
        tabs.addTab(responseTabInfo);

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(IdeaTopic.DELETE_ALL_DATA, (IdeaTopic.DeleteAllDataEventListener) () -> {
            httpResponseHeaderView.setText("");
            httpResponseView.reset();
        });
    }


    private void onHttpResponseEvent(String requestId, InvokeResponseModel invokeResponseModel) {
        SwingUtilities.invokeLater(() -> {
            byte[] response = invokeResponseModel.getData();
            httpResponseHeaderView.setText(invokeResponseModel.headerToString());
            String contentType = "text/plain";
            for (InvokeResponseModel.Header header : invokeResponseModel.getHeader()) {
                if ("content-type".equalsIgnoreCase(header.getKey()) && !StringUtils.isEmpty(header.getValue())) {
                    contentType = header.getValue();
                }
            }
            httpResponseView.setResponseData(contentType, response);
        });
    }
}
