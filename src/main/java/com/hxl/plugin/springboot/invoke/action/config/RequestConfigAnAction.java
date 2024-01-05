package com.hxl.plugin.springboot.invoke.action.config;

import com.hxl.plugin.springboot.invoke.view.dialog.RequestInfoConfigDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

public class RequestConfigAnAction  extends AnAction {
    private Project project;
    public RequestConfigAnAction(Project project, Tree tree) {
        super("Config","Config", AllIcons.General.Settings);
        this.project =project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }
}
