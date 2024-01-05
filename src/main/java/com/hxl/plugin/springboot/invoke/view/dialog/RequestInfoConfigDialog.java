package com.hxl.plugin.springboot.invoke.view.dialog;

import com.hxl.plugin.springboot.invoke.bean.RequestEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RequestInfoConfigDialog extends DialogWrapper {
    private JTextField envName;
    private JPanel root;
    private JTextField prefix;
    private RequestEnvironment requestEnvironment;

    public RequestInfoConfigDialog(@Nullable Project project, RequestEnvironment requestEnvironment) {
        super(project);
        this.requestEnvironment = requestEnvironment;
        setSize(350,220);
        init();
        this.envName.setText(requestEnvironment.getEnvironmentName());
        this.prefix.setText(requestEnvironment.getPrefix());
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        requestEnvironment.setPrefix(prefix.getText());
        requestEnvironment.setEnvironmentName(envName.getText());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return root;
    }
}
