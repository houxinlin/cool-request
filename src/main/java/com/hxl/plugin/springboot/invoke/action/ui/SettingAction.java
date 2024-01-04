package com.hxl.plugin.springboot.invoke.action.ui;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SettingAction extends BaseLanguageAnAction {
    private final IToolBarViewEvents iViewEvents;

    public SettingAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("setting"), () -> ResourceBundleUtils.getString("setting"), AllIcons.General.Settings);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.openSettingView();
    }
}
