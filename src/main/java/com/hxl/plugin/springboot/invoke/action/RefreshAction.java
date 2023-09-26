package com.hxl.plugin.springboot.invoke.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
