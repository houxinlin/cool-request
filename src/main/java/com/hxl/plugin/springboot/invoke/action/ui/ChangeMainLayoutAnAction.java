package com.hxl.plugin.springboot.invoke.action.ui;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import org.jetbrains.annotations.NotNull;

public class ChangeMainLayoutAnAction extends AnAction {
    public ChangeMainLayoutAnAction() {
        super("Change Layout", "Change layout", AllIcons.Debugger.RestoreLayout);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ApplicationManager.getApplication().getMessageBus().syncPublisher(IdeaTopic.CHANGE_LAYOUT).event();

    }
}
