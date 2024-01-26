package com.cool.request.tool;

import com.cool.request.Constant;
import com.cool.request.IdeaTopic;
import com.cool.request.state.SettingPersistentState;
import com.cool.request.state.SettingsState;
import com.cool.request.utils.AnActionCallback;
import com.cool.request.utils.MultipleMap;
import com.cool.request.view.ToolComponentPage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ToggleActionButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MainToolWindows extends SimpleToolWindowPanel implements ToolActionPageSwitcher {
    private MainToolWindowsActionManager mainToolWindowsActionManager;

    private final MultipleMap<MainToolWindowsAction, JComponent, Boolean> actionButtonBooleanMultipleMap = new MultipleMap<>();
    private final Project project;

    public MainToolWindows(Project project) {
        super(false);
        this.project = project;
        ProviderManager.registerProvider(ToolActionPageSwitcher.class, Constant.ToolActionPageSwitcherKey, this, project);

        ApplicationManager.getApplication().getMessageBus()
                .connect().subscribe(IdeaTopic.COOL_REQUEST_SETTING_CHANGE, (IdeaTopic.BaseListener) this::init);
        init();
    }

    private void init() {
        SettingsState state = SettingPersistentState.getInstance().getState();
        if ((mainToolWindowsActionManager != null) &&
                (!state.mergeApiAndRequest) &&
                (mainToolWindowsActionManager instanceof DefaultMainToolWindowsActionManager)) {
            return;
        }
        initToolView(!state.mergeApiAndRequest ?
                new DefaultMainToolWindowsActionManager(project) :
                new MergeApiAndRequestToolWindowsActionManager(project));
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
        actionButtonBooleanMultipleMap.clear();
        if (getToolbar() != null) {
            remove(getToolbar());
        }

        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        for (MainToolWindowsAction action : mainToolWindowsActionManager.getActions()) {
            if (action.getViewFactory() != null) {
                defaultActionGroup.add(new ToolAnActionButton(action));
                JComponent component = action.isLazyLoad() ? null : action.getViewFactory().get();
                actionButtonBooleanMultipleMap.put(action, component, false);
                continue;
            }
            defaultActionGroup.add(new BaseAnAction(action));
        }
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("toolbar@MainToolWindows", defaultActionGroup, true);
        actionToolbar.setMiniMode(false);
        actionToolbar.setMinimumButtonSize(new Dimension(28, 28));
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());
        if (!mainToolWindowsActionManager.getActions().isEmpty()) {
            actionButtonBooleanMultipleMap.setSecondValue(mainToolWindowsActionManager.getActions().get(0), true);
            switchPage(mainToolWindowsActionManager.getActions().get(0), null);
            getToolbar().invalidate();
            getToolbar().repaint();
        }
        revalidate();
        invalidate();
        repaint();
        updateUI();

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

    private static class BaseAnAction extends AnAction {
        private final MainToolWindowsAction mainToolWindowsAction;

        public BaseAnAction(MainToolWindowsAction mainToolWindowsAction) {
            super(mainToolWindowsAction.getName(), mainToolWindowsAction.getName(), mainToolWindowsAction.getIcon());
            this.mainToolWindowsAction = mainToolWindowsAction;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            AnActionCallback callback = mainToolWindowsAction.getCallback();
            if (callback != null) callback.actionPerformed(e);
        }
    }

    private class ToolAnActionButton extends ToggleActionButton {
        private final MainToolWindowsAction mainToolWindowsAction;

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
