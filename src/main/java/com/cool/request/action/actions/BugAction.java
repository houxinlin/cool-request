package com.cool.request.action.actions;

import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.dialog.BugDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;


public class BugAction extends BaseAnAction {
    /**
     * BugAction is a class that extends BaseAnAction. It represents an action related to bugs in the system.
     * This action, when triggered, opens up a BugDialog.
     */
    public BugAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("bug"), () -> ResourceBundleUtils.getString("bug"), MyIcons.DEBUG);
    }

    /**
     * This method is triggered when the associated action is performed.
     * It creates a new BugDialog and displays it.
     *
     * @param anActionEvent The event object associated with the action.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        BugDialog bugDialog = new BugDialog(anActionEvent.getProject());
        bugDialog.show();
    }
}
