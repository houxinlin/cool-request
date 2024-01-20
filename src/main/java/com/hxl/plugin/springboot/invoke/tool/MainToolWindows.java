package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.utils.MultipleMap;
import com.hxl.plugin.springboot.invoke.view.ToolComponentPage;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ToggleActionButton;

import javax.swing.*;
import java.awt.*;

public class MainToolWindows extends SimpleToolWindowPanel implements ToolActionPageSwitcher {
    private MainToolWindowsActionManager mainToolWindowsActionManager;

    private MultipleMap<MainToolWindowsAction, JComponent, Boolean> actionButtonBooleanMultipleMap = new MultipleMap<>();

    public MainToolWindows(Project project) {
        super(false);
        ProviderManager.registerProvider(ToolActionPageSwitcher.class,Constant.ToolActionPageSwitcherKey, this, project);

        initToolView(new DefaultMainToolWindowsActionManager(project));
    }

    @Override
    public void goToByName(String name, Object attachData) {
        for (MainToolWindowsAction mainToolWindowsAction : actionButtonBooleanMultipleMap.keySet()) {
            if (mainToolWindowsAction.getName().equalsIgnoreCase(name)) {
                switchPage(mainToolWindowsAction, attachData);
                return;
            }
        }
    }

    private void initToolView(MainToolWindowsActionManager mainToolWindowsActionManager) {
        this.mainToolWindowsActionManager = mainToolWindowsActionManager;
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        for (MainToolWindowsAction action : mainToolWindowsActionManager.getActions()) {
            defaultActionGroup.add(new ToolAnActionButton(action));
            actionButtonBooleanMultipleMap.put(action, null, false);
        }
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("main_tool_place", defaultActionGroup, true);
        actionToolbar.setMiniMode(false);
        actionToolbar.setMinimumButtonSize(new Dimension(28, 28));
        setToolbar(actionToolbar.getComponent());

        if (mainToolWindowsActionManager.getActions().size() > 0) {
            actionButtonBooleanMultipleMap.put(mainToolWindowsActionManager.getActions().get(0), null, true);
            switchPage(mainToolWindowsActionManager.getActions().get(0), null);
            getToolbar().invalidate();
            getToolbar().repaint();
        }

    }

    private void switchPage(MainToolWindowsAction mainToolWindowsAction, Object attachData) {
        actionButtonBooleanMultipleMap.setAllSecondValue(false);
        JComponent view = actionButtonBooleanMultipleMap.getFirstValue(mainToolWindowsAction);

        if (view == null) view = mainToolWindowsAction.getViewFactory().get();
        actionButtonBooleanMultipleMap.put(mainToolWindowsAction, view, true);
        if (view instanceof ToolComponentPage) {
            ((ToolComponentPage) view).setAttachData(attachData);
        }
        setContent(view);
    }

    private class ToolAnActionButton extends ToggleActionButton {
        private MainToolWindowsAction mainToolWindowsAction;

        public ToolAnActionButton(MainToolWindowsAction action) {
            super(action.getName(), action.getIcon());
            this.mainToolWindowsAction = action;
        }

        @Override
        public boolean isSelected(AnActionEvent e) {
            return actionButtonBooleanMultipleMap.getSecondValue(this.mainToolWindowsAction);
        }

        @Override
        public void setSelected(AnActionEvent e, boolean state) {
            switchPage(mainToolWindowsAction, null);
        }
    }
}
