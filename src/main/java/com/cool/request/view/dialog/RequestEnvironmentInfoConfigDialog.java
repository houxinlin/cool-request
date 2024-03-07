package com.cool.request.view.dialog;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.page.FormDataRequestBodyPage;
import com.cool.request.view.page.FormUrlencodedRequestBodyPage;
import com.cool.request.view.page.RequestHeaderPage;
import com.cool.request.view.page.UrlPanelParamPage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class RequestEnvironmentInfoConfigDialog extends DialogWrapper {
    private JTextField envName;
    private JPanel root;
    private JTextField hostAddress;
    private JLabel nameLabel;
    private JLabel hostNameLabel;
    private JTabbedPane tabbedPane1;
    private JPanel globalHeaderPanel;
    private JPanel globalUrlParam;
    private JTabbedPane tabbedPane2;
    private JPanel globalFormDataPanel;
    private JPanel globalFormUrlencodedPanel;
    private RequestEnvironment requestEnvironment;

    private Project project;

    private RequestEnvironmentInfoConfigDialog(@Nullable Project project, RequestEnvironment requestEnvironment) {
        super(project);
        this.project = project;
        this.requestEnvironment = requestEnvironment;
        setSize(600, 500);
        init();
        this.envName.setText(requestEnvironment.getEnvironmentName());
        this.hostAddress.setText(requestEnvironment.getHostAddress());

        nameLabel.setText(ResourceBundleUtils.getString("environment.name"));
        hostNameLabel.setText(ResourceBundleUtils.getString("host.address"));

        new ComponentValidator(getDisposable()).withValidator(() -> {
            if (StringUtils.isUrl(hostAddress.getText())) return null;
            return new ValidationInfo("Invalid URL");
        }).andStartOnFocusLost().installOn(hostAddress);
        hostAddress.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ComponentValidator.getInstance(hostAddress).ifPresent(v -> v.revalidate());
            }
        });
    }

    public static void showDialog(Project project, RequestEnvironment requestEnvironment) {
        new RequestEnvironmentInfoConfigDialog(project, requestEnvironment).show();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return ComponentValidator.getInstance(hostAddress).get().getValidationInfo();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        requestEnvironment.setHostAddress(hostAddress.getText());
        requestEnvironment.setEnvironmentName(envName.getText());

        requestEnvironment.getHeader().clear();
        requestEnvironment.getHeader().addAll(((RequestHeaderPage) globalHeaderPanel).getTableMap());

        requestEnvironment.getUrlParam().clear();
        requestEnvironment.getUrlParam().addAll(((UrlPanelParamPage) globalUrlParam).getTableMap());

        requestEnvironment.getFormData().clear();
        requestEnvironment.getFormData().addAll(((FormDataRequestBodyPage) globalFormDataPanel).getFormData());

        requestEnvironment.getFormUrlencoded().clear();
        requestEnvironment.getFormUrlencoded().addAll(((FormUrlencodedRequestBodyPage) globalFormUrlencodedPanel).getTableMap());

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return root;
    }

    private void createUIComponents() {
        globalHeaderPanel = new RequestHeaderPage(project);
        globalUrlParam = new UrlPanelParamPage(project);
        globalFormDataPanel = new FormDataRequestBodyPage(project);
        globalFormUrlencodedPanel = new FormUrlencodedRequestBodyPage(project);

        ((RequestHeaderPage) globalHeaderPanel).setTableData(requestEnvironment.getHeader(), false);
        ((UrlPanelParamPage) globalUrlParam).setTableData(requestEnvironment.getUrlParam(), false);
        ((FormDataRequestBodyPage) globalFormDataPanel).setFormData(requestEnvironment.getFormData(), false);
        ((FormUrlencodedRequestBodyPage) globalFormUrlencodedPanel).setTableData(requestEnvironment.getFormUrlencoded(), false);

    }
}
