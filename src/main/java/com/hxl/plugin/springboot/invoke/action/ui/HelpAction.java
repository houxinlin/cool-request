package com.hxl.plugin.springboot.invoke.action.ui;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

public class HelpAction extends AnAction {
    public HelpAction() {
        super(()->"a", AllIcons.Actions.Help);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {


    }
}
