package com.hxl.plugin.springboot.invoke.view.dialog;

import com.hxl.plugin.springboot.invoke.bean.RequestEnvironment;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.page.RequestHeaderPage;
import com.hxl.plugin.springboot.invoke.view.page.UrlParamPage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RequestInfoConfigDialog extends DialogWrapper {
    private JTextField envName;
    private JPanel root;
    private JTextField hostAddress;
    private JLabel nameLabel;
    private JLabel hostNameLabel;
    private JTabbedPane tabbedPane1;
    private JPanel globalHeaderPanel;
    private JPanel globalUrlParam;
    private RequestEnvironment requestEnvironment;

    private Project project;

    public RequestInfoConfigDialog(@Nullable Project project, RequestEnvironment requestEnvironment) {
        super(project);
        this.project = project;
        this.requestEnvironment = requestEnvironment;
        setSize(600, 500);
        init();
        this.envName.setText(requestEnvironment.getEnvironmentName());
        this.hostAddress.setText(requestEnvironment.getHostAddress());

        nameLabel.setText(ResourceBundleUtils.getString("environment.name"));
        hostNameLabel.setText(ResourceBundleUtils.getString("host.address"));

    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        requestEnvironment.setHostAddress(hostAddress.getText());
        requestEnvironment.setEnvironmentName(envName.getText());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return root;
    }

    private void createUIComponents() {
        globalHeaderPanel = new RequestHeaderPage(project);
        globalUrlParam = new UrlParamPage(project);
        globalHeaderPanel.setBorder(null);
        globalUrlParam.setBorder(null);
    }
}
