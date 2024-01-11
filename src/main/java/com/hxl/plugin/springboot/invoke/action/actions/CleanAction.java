package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * delete tree data
 */
public class CleanAction extends BaseAnAction {
    private final IToolBarViewEvents iViewEvents;

    /**
     * CleanAction is a class that extends BaseAnAction.
     * It represents an action related to delete tree data in the system.
     * @param project   The project in which the action is being created.
     * @param iViewEvents The view events.
     */
    public CleanAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("delete.all"), () -> ResourceBundleUtils.getString("delete.all"), AllIcons.Actions.GC);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.clearAllData();
    }
}
