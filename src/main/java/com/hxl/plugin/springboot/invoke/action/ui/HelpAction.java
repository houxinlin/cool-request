package com.hxl.plugin.springboot.invoke.action.ui;


import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

public class HelpAction extends AnAction {
    private final IToolBarViewEvents iViewEvents;

    public HelpAction(IToolBarViewEvents iViewEvents) {
        super(() -> "Help", AllIcons.Actions.Help);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.pluginHelp();
    }
}
