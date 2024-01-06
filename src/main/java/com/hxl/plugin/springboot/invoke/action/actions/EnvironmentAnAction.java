package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.view.dialog.EnvironmentConfigDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class EnvironmentAnAction  extends AnAction {
    public EnvironmentAnAction() {
        super("Environment","Environment", AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new EnvironmentConfigDialog(e.getProject()).show();
    }
}
