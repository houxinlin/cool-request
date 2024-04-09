/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ScriptCodePage.java is part of Cool Request
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

package com.cool.request.view.page;

import com.cool.request.action.actions.DynamicAnAction;
import com.cool.request.action.actions.DynamicIconToggleActionButton;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.components.http.script.CompilationException;
import com.cool.request.components.http.script.JavaCodeEngine;
import com.cool.request.components.http.script.dialog.ScriptEditorDialog;
import com.cool.request.utils.*;
import com.cool.request.view.widget.JavaEditorTextField;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public class ScriptCodePage extends JPanel {
    private final JavaEditorTextField requestTextEditPage;
    private final JavaEditorTextField responseTextEditPage;
    private final TabInfo preTabInfo;
    private final TabInfo postTabInfo;
    private final Project project;

    public ScriptCodePage(Project project) {
        this.setLayout(new BorderLayout());
        requestTextEditPage = new JavaEditorTextField(project);
        responseTextEditPage = new JavaEditorTextField(project);
        preTabInfo = new TabInfo(new ScriptPage(requestTextEditPage, JavaCodeEngine.REQUEST_CLASS));
        postTabInfo = new TabInfo(new ScriptPage(responseTextEditPage, JavaCodeEngine.RESPONSE_CLASS));
        this.project = project;
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        jbTabs.addTab(preTabInfo.setText("Request"));
        jbTabs.addTab(postTabInfo.setText("Response"));
        add(jbTabs.getComponent());
        MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();
        connect.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, (CoolRequestIdeaTopic.BaseListener) this::loadText);
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), connect);
        loadText();
    }

    private void loadText() {
        preTabInfo.setText(ResourceBundleUtils.getString("pre.script"));
        postTabInfo.setText(ResourceBundleUtils.getString("post.script"));

    }

    public String getRequestScriptText() {
        return this.requestTextEditPage.getText();
    }

    public String getResponseScriptText() {
        return this.responseTextEditPage.getText();
    }

    public void setScriptText(String requestScript, String responseScript) {
        this.requestTextEditPage.setText(requestScript);
        this.responseTextEditPage.setText(responseScript);
    }

    class ScriptPage extends SimpleToolWindowPanel {

        public ScriptPage(JavaEditorTextField javaEditorTextField, String className) {
            super(true);
            DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
            defaultActionGroup.add(new CompileAnAction(project, javaEditorTextField, className));
            defaultActionGroup.add(new InstallLibraryAnAction(project));
            defaultActionGroup.add(new WindowAction(project, javaEditorTextField));
            defaultActionGroup.add(new CodeAnAction(project, javaEditorTextField, className));
            defaultActionGroup.add(new EnabledLibrary());
            defaultActionGroup.add(new HelpAnAction(project));
            ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("scpipt@ScriptPage", defaultActionGroup, false);
            toolbar.setTargetComponent(this);
            setToolbar(toolbar.getComponent());
            setContent(javaEditorTextField);
        }
    }

    public static class WindowAction extends DynamicAnAction implements Consumer<String> {
        private final JavaEditorTextField javaEditorTextField;

        public WindowAction(Project project, JavaEditorTextField javaEditorTextField) {
            super(project, () -> "Window", KotlinCoolRequestIcons.INSTANCE.getWINDOW());
            this.javaEditorTextField = javaEditorTextField;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            new ScriptEditorDialog(javaEditorTextField.getText(), getProject(), this).show();
        }

        @Override
        public void accept(String s) {
            javaEditorTextField.setText(s);
        }
    }

    public static class CodeAnAction extends DynamicAnAction {
        private final String targetClassName;
        private final JavaEditorTextField javaEditorTextField;

        public CodeAnAction(Project project, JavaEditorTextField javaEditorTextField, String targetClassName) {
            super(project, () -> ResourceBundleUtils.getString("template"), KotlinCoolRequestIcons.INSTANCE.getTEMPLATE());
            this.targetClassName = targetClassName;
            this.javaEditorTextField = javaEditorTextField;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            String code = JavaCodeEngine.REQUEST_CLASS.equals(targetClassName) ?
                    new String(Objects.requireNonNull(ClassResourceUtils.read("/plugin-script-request.java"))) :
                    new String(Objects.requireNonNull(ClassResourceUtils.read("/plugin-script-response.java")));
            javaEditorTextField.setText(code);
        }
    }

    public static class EnabledLibrary extends DynamicIconToggleActionButton {
        public EnabledLibrary() {
            super(() -> ResourceBundleUtils.getString("enabled.library"), KotlinCoolRequestIcons.INSTANCE.getDEPENDENT());
        }

        @Override
        public boolean isSelected(@NotNull AnActionEvent e) {
            SettingsState settingsState = SettingPersistentState.getInstance().getState();
            return settingsState.enabledScriptLibrary;
        }

        @Override
        public void setSelected(@NotNull AnActionEvent e, boolean state) {
            SettingsState settingsState = SettingPersistentState.getInstance().getState();
            settingsState.enabledScriptLibrary = state;

        }

    }

    public static class InstallLibraryAnAction extends DynamicAnAction {
        public InstallLibraryAnAction(Project project) {
            super(project, () -> "Install Library", KotlinCoolRequestIcons.INSTANCE.getLIBRARY());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            String msg = ResourceBundleUtils.getString("install.lib");
            int result = Messages.showOkCancelDialog(e.getProject(), msg,
                    ResourceBundleUtils.getString("tip"), "Install", "No", KotlinCoolRequestIcons.INSTANCE.getLIBRARY().invoke());
            if (0 == result) {
                ClassResourceUtils.copyTo(getClass().getResource(CoolRequestConfigConstant.CLASSPATH_SCRIPT_API_PATH),
                        CoolRequestConfigConstant.CONFIG_SCRIPT_LIB_PATH.toString());
                ProjectUtils.addDependency(e.getProject(), CoolRequestConfigConstant.CONFIG_SCRIPT_LIB_PATH.toString());
            }
        }
    }

    /**
     * 编译脚本，用于验证代码是否正确
     */
    public static class CompileAnAction extends DynamicAnAction {
        private final JavaEditorTextField javaEditorTextField;
        private final String className;

        public CompileAnAction(Project project, JavaEditorTextField javaEditorTextField, String className) {
            super(project, () -> "Compile Test", KotlinCoolRequestIcons.INSTANCE.getBUILD());
            this.className = className;
            this.javaEditorTextField = javaEditorTextField;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {

            JavaCodeEngine javaCodeEngine = new JavaCodeEngine(getProject());
            ProgressManager.getInstance().run(new Task.Backgroundable(getProject(), "Compile...") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        if (StringUtils.isEmpty(javaEditorTextField.getText())) return;
                        javaCodeEngine.javac(javaEditorTextField.getText(), className);
                    } catch (Exception ex) {
                        if (ex instanceof CompilationException) {
                            SwingUtilities.invokeLater(() -> Messages.showErrorDialog(ex.getMessage(), "Compile Fail"));
                        }
                    }
                }
            });

        }
    }


    public static class HelpAnAction extends DynamicAnAction {
        public HelpAnAction(Project project) {
            super(project, () -> "Help", KotlinCoolRequestIcons.INSTANCE.getHELP());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {

            WebBrowseUtils.browse("https://plugin.houxinlin.com/docs/tutorial-script/script");
        }
    }
}
