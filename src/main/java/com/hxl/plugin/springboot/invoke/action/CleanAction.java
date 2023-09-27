package com.hxl.plugin.springboot.invoke.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;


public class CleanAction extends DumbAwareAction {
    public CleanAction() {
        super(() -> "Delete ALL", AllIcons.Actions.GC);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
