package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.view.dialog.AboutDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
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
