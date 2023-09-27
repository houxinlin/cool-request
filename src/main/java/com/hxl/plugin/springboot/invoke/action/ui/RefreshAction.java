package com.hxl.plugin.springboot.invoke.action.ui;

import com.hxl.plugin.springboot.invoke.utils.ProjectUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.CoreProgressManager;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;


public class RefreshAction  extends DumbAwareAction {
    public RefreshAction() {
        super(() -> "Refresh", AllIcons.Actions.Refresh);
        getTemplatePresentation().setText("Refresh");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
