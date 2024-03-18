package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

public class CoolRequestAnAction extends BaseAnAction {
    public CoolRequestAnAction(Project project) {
        super(project, () -> "CoolRequest", CoolRequestIcons.MAIN);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(
                new VersionAnAction(project),
                new BugAnAction(project));

        JBPopupFactory.getInstance().createActionGroupPopup(
                        null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false, null, 10, null, "popup@RefreshAction")
                .showUnderneathOf(e.getInputEvent().getComponent());
    }

}
