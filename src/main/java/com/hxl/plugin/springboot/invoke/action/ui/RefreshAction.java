package com.hxl.plugin.springboot.invoke.action.ui;

import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;


public class RefreshAction  extends DumbAwareAction {
    private IToolBarViewEvents iViewEvents;
    public RefreshAction(IToolBarViewEvents iViewEvents) {
        super(() -> "Refresh", AllIcons.Actions.Refresh);
        this.iViewEvents =iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.refreshTree();
    }
}
