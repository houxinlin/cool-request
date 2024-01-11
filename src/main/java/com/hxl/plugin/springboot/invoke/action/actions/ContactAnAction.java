package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.view.dialog.ContactDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;



public class ContactAnAction extends BaseAnAction{
    /**
     * ContactAnAction is a class that extends BaseAnAction.
     * @param project The project in which the action is being created.
     */
    public ContactAnAction(Project project) {
        super(project, ()->"author", ()->"author", AllIcons.CodeWithMe.CwmAccessOn);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new ContactDialog(getProject()).show();
    }
}
