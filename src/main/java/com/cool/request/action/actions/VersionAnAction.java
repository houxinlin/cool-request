package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.view.dialog.AboutDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class VersionAnAction extends BaseAnAction {
    public VersionAnAction(Project project) {
        super(project, () -> "Version", CoolRequestIcons.VERSION);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new AboutDialog(getProject()).show();
    }

}
