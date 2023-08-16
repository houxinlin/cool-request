package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.view.page.HTTPResponseHeaderView;
import com.hxl.plugin.springboot.invoke.view.page.HTTPResponseView;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPResponseView  extends JPanel {
    private final Project project;
    public MainBottomHTTPResponseView( final Project project) {
        this.project=project;
        initUI();
    }

    private void initUI() {
        JBTabsImpl  tabs = new JBTabsImpl(project);
        {
            TabInfo tabInfo = new TabInfo(new HTTPResponseView(project) );
            tabInfo.setText("Response");
            tabs.addTab(tabInfo);
        }

        TabInfo headerView = new TabInfo(new HTTPResponseHeaderView(project) );
        headerView.setText("Header");
        tabs.addTab(headerView);

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);

    }
}
