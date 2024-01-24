package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
import com.hxl.plugin.springboot.invoke.state.SettingsState;
import com.hxl.plugin.springboot.invoke.utils.AnActionCallback;
import com.hxl.plugin.springboot.invoke.utils.MultipleMap;
import com.hxl.plugin.springboot.invoke.view.ToolComponentPage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.NlsActions;
import com.intellij.ui.ToggleActionButton;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING;

public class MainToolWindows extends SimpleToolWindowPanel implements ToolActionPageSwitcher {
    private MainToolWindowsActionManager mainToolWindowsActionManager;

    private MultipleMap<MainToolWindowsAction, JComponent, Boolean> actionButtonBooleanMultipleMap = new MultipleMap<>();
    private Project project;

    public MainToolWindows(Project project) {
        super(false);
        this.project = project;
        ProviderManager.registerProvider(ToolActionPageSwitcher.class, Constant.ToolActionPageSwitcherKey, this, project);

        ApplicationManager.getApplication().getMessageBus().connect().subscribe(IdeaTopic.COOL_REQUEST_SETTING_CHANGE, (IdeaTopic.BaseListener) () -> {
            init();
        });
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
        setToolbar(actionToolbar.getComponent());
        if (mainToolWindowsActionManager.getActions().size() > 0) {
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

    private class BaseAnAction extends AnAction {
        private MainToolWindowsAction mainToolWindowsAction;

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
