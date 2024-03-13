package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.view.dialog.PayDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class PayAnAction  extends BaseAnAction{
    public PayAnAction(Project project) {
        super(project, ()->"", CoolRequestIcons.MONEY);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new PayDialog(getProject()).show();
    }
}
