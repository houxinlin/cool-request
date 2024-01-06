package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


public class RefreshAction extends BaseAnAction {
    private final IToolBarViewEvents iViewEvents;

    public RefreshAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("refresh"), () -> ResourceBundleUtils.getString("refresh"), AllIcons.Actions.Refresh);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.refreshTree();
    }
}
