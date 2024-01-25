package com.cool.request.action.actions;

import com.cool.request.icons.MyIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.events.IToolBarViewEvents;
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
        super(project, () -> ResourceBundleUtils.getString("delete.all"),
                () -> ResourceBundleUtils.getString("delete.all"), MyIcons.DELETE);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.clearAllData();
    }
}
