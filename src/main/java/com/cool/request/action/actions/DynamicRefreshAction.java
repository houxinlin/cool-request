package com.cool.request.action.actions;

import com.cool.request.Constant;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class DynamicRefreshAction extends AnAction {
    private final Project project;

    public DynamicRefreshAction(Project project) {
        super("Dynamic Refresh", "Dynamic refresh", MyIcons.LIGHTNING);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Objects.requireNonNull(project.getUserData(Constant.UserProjectManagerKey)).refreshComponents();
    }
}
