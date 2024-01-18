package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class DynamicRefreshAction extends AnAction {
    private final Project project;

    public DynamicRefreshAction(Project project) {
        super(ResourceBundleUtils.getString("refresh.dynamic"), ResourceBundleUtils.getString("refresh.dynamic"), MyIcons.LIGHTNING);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Objects.requireNonNull(project.getUserData(Constant.UserProjectManagerKey)).refreshComponents();
    }
}
