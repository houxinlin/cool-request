package com.cool.request.view.page;

import com.cool.request.IdeaTopic;
import com.cool.request.action.actions.BaseAnAction;
import com.cool.request.script.CompilationException;
import com.cool.request.script.JavaCodeEngine;
import com.cool.request.script.dialog.ScriptEditorDialog;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.WebBrowseUtils;
import com.cool.request.view.widget.JavaEditorTextField;
import com.intellij.icons.AllIcons;
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
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ScriptCodePage extends JPanel {
    private final JavaEditorTextField requestTextEditPage;
    private final JavaEditorTextField responseTextEditPage;
    private TabInfo preTabInfo;
    private TabInfo postTabInfo;
    private Project project;

    public ScriptCodePage(Project project) {
        this.setLayout(new BorderLayout());
        requestTextEditPage = new JavaEditorTextField("", project);
        responseTextEditPage = new JavaEditorTextField("", project);
        preTabInfo = new TabInfo(new ScriptPage(requestTextEditPage, JavaCodeEngine.REQUEST_CLASS));
        postTabInfo = new TabInfo(new ScriptPage(responseTextEditPage, JavaCodeEngine.RESPONSE_CLASS));
        this.project = project;
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        jbTabs.addTab(preTabInfo.setText("Request"));
        jbTabs.addTab(postTabInfo.setText("Response"));
        add(jbTabs.getComponent());

        ApplicationManager.getApplication().getMessageBus().connect().subscribe(IdeaTopic.COOL_REQUEST_SETTING_CHANGE, (IdeaTopic.BaseListener) () -> loadText());
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
        private JavaEditorTextField javaEditorTextField;

        public ScriptPage(JavaEditorTextField javaEditorTextField, String className) {
            super(true);
            this.javaEditorTextField = javaEditorTextField;
            DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
            defaultActionGroup.add(new CompileAnAction(project, javaEditorTextField, className));
            defaultActionGroup.add(new MainAnAction(project, javaEditorTextField));
            defaultActionGroup.add(new HelpAnAction(project));
            ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("scpipt@ScriptPage", defaultActionGroup, false);
            toolbar.setTargetComponent(this);
            setToolbar(toolbar.getComponent());
            setContent(javaEditorTextField);
        }
    }

    class MainAnAction extends BaseAnAction implements Consumer<String> {
        private JavaEditorTextField javaEditorTextField;

        public MainAnAction(Project project, JavaEditorTextField javaEditorTextField) {
            super(project, () -> "Dialog", AllIcons.Actions.MoveToWindow);
            this.javaEditorTextField = javaEditorTextField;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            new ScriptEditorDialog(javaEditorTextField.getText(), project, this).show();
        }

        @Override
        public void accept(String s) {
            javaEditorTextField.setText(s);
        }
    }

    class CompileAnAction extends BaseAnAction {
        private JavaEditorTextField javaEditorTextField;
        private String className;

        public CompileAnAction(Project project, JavaEditorTextField javaEditorTextField, String className) {
            super(project, () -> "compile", AllIcons.Actions.Compile);
            this.className = className;
            this.javaEditorTextField = javaEditorTextField;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            JavaCodeEngine javaCodeEngine = new JavaCodeEngine();
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "compile...") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        if (StringUtils.isEmpty(javaEditorTextField.getText())) return;
                        javaCodeEngine.javac(javaEditorTextField.getText(), className);
                        SwingUtilities.invokeLater(() -> Messages.showOkCancelDialog("Compile Success", "Tip", MyIcons.MAIN));
                    } catch (Exception ex) {
                        if (ex instanceof CompilationException) {
                            SwingUtilities.invokeLater(() -> Messages.showErrorDialog(ex.getMessage(), "Compile Fail"));
                        }
                    }
                }
            });

        }
    }

    class HelpAnAction extends BaseAnAction {
        public HelpAnAction(Project project) {
            super(project, () -> "Help", AllIcons.Actions.Help);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            WebBrowseUtils.browse("https://plugin.houxinlin.com/script");
        }
    }
}
