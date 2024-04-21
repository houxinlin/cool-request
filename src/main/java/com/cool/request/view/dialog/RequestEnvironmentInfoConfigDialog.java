/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestEnvironmentInfoConfigDialog.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.dialog;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.components.http.script.JavaCodeEngine;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.page.*;
import com.cool.request.view.table.RowDataState;
import com.cool.request.view.widget.JavaEditorTextField;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.function.Consumer;

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
    private JTabbedPane script;
    private final RequestEnvironment requestEnvironment;

    private final Project project;

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
                ComponentValidator.getInstance(hostAddress).ifPresent(ComponentValidator::revalidate);
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
        requestEnvironment.getHeader().addAll(((RequestHeaderPage) globalHeaderPanel).getTableMap(RowDataState.all));

        requestEnvironment.getUrlParam().clear();
        requestEnvironment.getUrlParam().addAll(((UrlPanelParamPage) globalUrlParam).getTableMap(RowDataState.all));

        requestEnvironment.getFormData().clear();
        requestEnvironment.getFormData().addAll(((FormDataRequestBodyPage) globalFormDataPanel).getFormData(RowDataState.all));

        requestEnvironment.getFormUrlencoded().clear();
        requestEnvironment.getFormUrlencoded().addAll(((FormUrlencodedRequestBodyPage) globalFormUrlencodedPanel).getTableMap(RowDataState.all));

    }

    static class ScriptPage extends SimpleToolWindowPanel {

        public ScriptPage(Project project,
                          JavaEditorTextField javaEditorTextField,
                          String className,
                          Consumer<String> consumer) {
            super(true);
            DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
            defaultActionGroup.add(new ScriptCodePage.CompileAnAction(project, javaEditorTextField, className));
            defaultActionGroup.add(new ScriptCodePage.InstallLibraryAnAction(project));
            defaultActionGroup.add(new ScriptCodePage.WindowAction(project, javaEditorTextField));

            defaultActionGroup.add(new ScriptCodePage.CodeAnAction(project, javaEditorTextField, className));
            defaultActionGroup.add(new ScriptCodePage.EnabledLibrary());
            defaultActionGroup.add(new ScriptCodePage.HelpAnAction(project));
            ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("scpipt@ScriptPage", defaultActionGroup, false);
            toolbar.setTargetComponent(this);
            setToolbar(toolbar.getComponent());
            setContent(javaEditorTextField);

            javaEditorTextField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(com.intellij.openapi.editor.event.@NotNull DocumentEvent event) {
                    consumer.accept(javaEditorTextField.getText());
                }
            });
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return root;
    }

    private JavaEditorTextField createJavaEditorTextField(String initText) {
        JavaEditorTextField javaEditorTextField = new JavaEditorTextField(project);
        javaEditorTextField.setText(initText);
        return javaEditorTextField;
    }

    private void createUIComponents() {

        script = new JBTabbedPane();
        ScriptPage requestPage = new ScriptPage(project, createJavaEditorTextField(requestEnvironment.getRequestScript()),
                JavaCodeEngine.REQUEST_CLASS,
                requestEnvironment::setRequestScript);
        ScriptPage responsePage = new ScriptPage(project, createJavaEditorTextField(requestEnvironment.getResponseScript()),
                JavaCodeEngine.RESPONSE_CLASS,
                requestEnvironment::setResponseScript);

        script.insertTab("Request", null, requestPage, "Request", 0);
        script.insertTab("Response", null, responsePage, "Response", 1);


        globalHeaderPanel = new RequestHeaderPage(project, getWindow());
        globalUrlParam = new UrlPanelParamPage(project);
        globalFormDataPanel = new FormDataRequestBodyPage(project,null);
        globalFormUrlencodedPanel = new FormUrlencodedRequestBodyPage(null);


//        ((RequestHeaderPage) globalHeaderPanel).setTableData(requestEnvironment.getHeader(), false);
        ((UrlPanelParamPage) globalUrlParam).setTableData(requestEnvironment.getUrlParam());
        ((FormDataRequestBodyPage) globalFormDataPanel).setFormData(requestEnvironment.getFormData());
        ((FormUrlencodedRequestBodyPage) globalFormUrlencodedPanel).setTableData(requestEnvironment.getFormUrlencoded());

    }
}
