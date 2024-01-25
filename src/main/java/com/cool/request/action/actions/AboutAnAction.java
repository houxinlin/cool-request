package com.cool.request.action.actions;

import com.cool.request.icons.MyIcons;
import com.cool.request.view.dialog.AboutDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AboutAnAction extends BaseAnAction {
    public AboutAnAction(Project project) {
        super(project, () -> "About", () -> "About", MyIcons.MAIN);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new AboutDialog(getProject()).show();
    }
}
