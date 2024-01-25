package com.cool.request.action.actions;

import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

public class SettingAction extends BaseAnAction {
    private final IToolBarViewEvents iViewEvents;

    public SettingAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("setting"), () -> ResourceBundleUtils.getString("setting")
                , MyIcons.SETTING_SMALL);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.openSettingView();
    }
}
