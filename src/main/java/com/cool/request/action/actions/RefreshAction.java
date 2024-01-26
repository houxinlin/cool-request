package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.LayeredIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class RefreshAction extends BaseAnAction {
    private final IToolBarViewEvents iViewEvents;
    public static final Icon ADD_WITH_DROPDOWN = new LayeredIcon(CoolRequestIcons.REFRESH, AllIcons.General.Dropdown);

    public RefreshAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("refresh"),
                () -> ResourceBundleUtils.getString("refresh"), ADD_WITH_DROPDOWN);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(new StaticRefreshAction(project, this.iViewEvents),
                new DynamicRefreshAction(project));
        defaultActionGroup.getTemplatePresentation().setIcon(ADD_WITH_DROPDOWN);
        defaultActionGroup.getTemplatePresentation().setText(ResourceBundleUtils.getString("refresh"));
        defaultActionGroup.registerCustomShortcutSet(CommonShortcuts.getNewForDialogs(), null);


        JBPopupFactory.getInstance().createActionGroupPopup(
                        null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false, null, 10, null, "popup@RefreshAction")
                .showUnderneathOf(e.getInputEvent().getComponent());

    }
}
