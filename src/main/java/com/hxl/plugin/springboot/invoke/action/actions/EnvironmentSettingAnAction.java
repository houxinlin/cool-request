package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.view.dialog.EnvironmentConfigDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * 点击环境设置
 */
public class EnvironmentSettingAnAction extends BaseAnAction {
    public EnvironmentSettingAnAction(Project project) {
        super(project, () -> "Setting", AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new EnvironmentConfigDialog(e.getProject()).show();
    }
}
