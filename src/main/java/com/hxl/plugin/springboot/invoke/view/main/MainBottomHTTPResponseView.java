package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.view.page.HTTPResponseHeaderView;
import com.hxl.plugin.springboot.invoke.view.page.HTTPResponseView;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPResponseView extends JPanel implements HttpResponseListener {
    private final Project project;
    private HTTPResponseView httpResponseView;
    private HTTPResponseHeaderView httpResponseHeaderView;
    public MainBottomHTTPResponseView(final Project project) {
        this.project = project;
        initUI();
    }

    private void initUI() {
        JBTabsImpl tabs = new JBTabsImpl(project);
        httpResponseHeaderView = new HTTPResponseHeaderView(project);
        TabInfo headerView = new TabInfo(httpResponseHeaderView);
        headerView.setText("Header");
        tabs.addTab(headerView);

        httpResponseView = new HTTPResponseView(project);
        TabInfo tabInfo = new TabInfo(httpResponseView);
        tabInfo.setText("Response");
        tabs.addTab(tabInfo);

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);

    }

    public void setResult(String header, String body) {
        httpResponseHeaderView.setText(header);
        httpResponseView.setResponseData(body.getBytes());
    }

    @Override
    public void onResponse(String requestId, InvokeResponseModel invokeResponseModel) {
        SwingUtilities.invokeLater(() -> {
            boolean format = false;
            for (InvokeResponseModel.Header header : invokeResponseModel.getHeader()) {
                if (header.getKey().equalsIgnoreCase("content-type")
                        && header.getValue().toLowerCase().contains("json")) {
                    format = true;
                    break;
                }
            }
            String response = invokeResponseModel.getData();
            httpResponseHeaderView.setText(invokeResponseModel.headerToString());
            httpResponseView.setResponseData(response.getBytes());
        });
    }
}
