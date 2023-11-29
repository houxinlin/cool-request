package com.hxl.plugin.springboot.invoke.action.ui;

import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class SettingAction extends AnAction {
    private final IToolBarViewEvents iViewEvents;

    public SettingAction(IToolBarViewEvents iViewEvents) {
        super(()->"Setting", AllIcons.General.Settings);
        this.iViewEvents=iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.openSettingView();
    }
}
