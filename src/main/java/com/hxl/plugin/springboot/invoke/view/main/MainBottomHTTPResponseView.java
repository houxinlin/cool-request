package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.utils.HeaderUtils;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.hxl.plugin.springboot.invoke.view.page.HTTPResponseHeaderView;
import com.hxl.plugin.springboot.invoke.view.page.HTTPResponseView;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import okhttp3.Headers;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        {
            httpResponseHeaderView = new HTTPResponseHeaderView(project);
            TabInfo headerView = new TabInfo(httpResponseHeaderView);
            headerView.setText("Header");
            tabs.addTab(headerView);
        }
        {
            httpResponseView = new HTTPResponseView(project);
            TabInfo tabInfo = new TabInfo(httpResponseView);
            tabInfo.setText("Response");
            tabs.addTab(tabInfo);
        }

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);

    }

    @Override
    public void onResponse(String requestId, List<InvokeResponseModel.Header> headers, String response) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder headerStringBuffer = new StringBuilder();
            boolean format = false;
            for (InvokeResponseModel.Header header : headers) {
                if (header.getKey().equalsIgnoreCase("content-type")
                        && header.getValue().toLowerCase().contains("json")) {
                    format = true;
                }
                headerStringBuffer.append(header.getKey()).append(": ").append(header.getValue());
                headerStringBuffer.append("\n");
            }
            httpResponseHeaderView.setText(headerStringBuffer.toString());
            httpResponseView.setText(format?ObjectMappingUtils.format(response):response);
        });
    }
}
