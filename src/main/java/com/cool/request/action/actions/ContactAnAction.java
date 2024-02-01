package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.dialog.ContactDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;



public class ContactAnAction extends BaseAnAction{
    /**
     * ContactAnAction is a class that extends BaseAnAction.
     * @param project The project in which the action is being created.
     */
    public ContactAnAction(Project project) {
        super(project, ()-> ResourceBundleUtils.getString("author"),
                ()->ResourceBundleUtils.getString("author"),
                CoolRequestIcons.CHAT);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new ContactDialog(getProject()).show();
    }
}
