package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LayeredIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;


public class RefreshAction extends BaseAnAction {
    private final IToolBarViewEvents iViewEvents;
    public static final Icon ADD_WITH_DROPDOWN = new LayeredIcon(AllIcons.Actions.Refresh, AllIcons.General.Dropdown);

    public RefreshAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("refresh"), () -> ResourceBundleUtils.getString("refresh"), ADD_WITH_DROPDOWN);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        DefaultActionGroup addGroup = new DefaultActionGroup(new StaticRefreshAction(project,this.iViewEvents), new DynamicRefreshAction(project));
        addGroup.getTemplatePresentation().setIcon(ADD_WITH_DROPDOWN);
        addGroup.getTemplatePresentation().setText(ResourceBundleUtils.getString("refresh"));
        addGroup.registerCustomShortcutSet(CommonShortcuts.getNewForDialogs(), null);

        JPopupMenu component = ActionManager.getInstance().createActionPopupMenu("type", addGroup).getComponent();

        if (e.getInputEvent() instanceof MouseEvent) {
            MouseEvent inputEvent = (MouseEvent) e.getInputEvent();
            component.show(e.getInputEvent().getComponent(), inputEvent.getX(), inputEvent.getY());
        }

    }
}
