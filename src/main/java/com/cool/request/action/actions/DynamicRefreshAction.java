package com.cool.request.action.actions;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.icons.CoolRequestIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class DynamicRefreshAction extends AnAction {
    private final Project project;

    public DynamicRefreshAction(Project project) {
        super("Dynamic Refresh", "Dynamic refresh", CoolRequestIcons.LIGHTNING);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.UserProjectManagerKey)).refreshComponents();
    }
}
