package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.Constant;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;


public class DynamicRefreshAction extends AnAction {
    private Project project;

    public DynamicRefreshAction(Project project) {
        super("Dynamic Refresh", "Dynamic Refresh", MyIcons.LIGHTNING);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project.getUserData(Constant.UserProjectManagerKey).refreshComponents();
    }
}
