package com.hxl.plugin.springboot.invoke.action.ui;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.dialog.BugDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;


public class BugAction extends BaseLanguageAnAction {
    public BugAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("bug"), () -> ResourceBundleUtils.getString("bug"), MyIcons.DEBUG);

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        BugDialog bugDialog = new BugDialog(anActionEvent.getProject());
        bugDialog.show();
    }
}
