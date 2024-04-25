/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MainToolWindows.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.tool;

import com.cool.request.action.actions.DynamicIconToggleActionButton;
import com.cool.request.common.bean.MultipleMap;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.view.ToolComponentPage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainToolWindows extends SimpleToolWindowPanel implements ToolActionPageSwitcher {
    private MainToolWindowsActionManager mainToolWindowsActionManager;

    private final MultipleMap<MainToolWindowsAction, JComponent, Boolean> actionButtonBooleanMultipleMap = new MultipleMap<>();
    private final Project project;
    private final Map<String, JComponent> viewCacheMap = new HashMap<>();
    private final DefaultActionGroup mainActionGroup = new DefaultActionGroup();

    public MainToolWindows(Project project) {
        super(false);
        this.project = project;
        ProviderManager.registerProvider(ToolActionPageSwitcher.class, CoolRequestConfigConstant.ToolActionPageSwitcherKey, this, project);

        MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();
        connect.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, this::init);
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), connect);
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

        mainActionGroup.removeAll();
        for (MainToolWindowsAction action : mainToolWindowsActionManager.getActions()) {
            if (action.getViewFactory() != null) {
                mainActionGroup.add(new ToolAnActionButton(action));

                JComponent component = action.isLazyLoad() ? null : action.getViewFactory().get();
                actionButtonBooleanMultipleMap.put(action, component, false);
                viewCacheMap.put(action.getName(), component);
                continue;
            }
            mainActionGroup.add(new BaseAnAction(action));
        }
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("toolbar@MainToolWindows", mainActionGroup, true);
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
    }

    private void switchPage(MainToolWindowsAction mainToolWindowsAction, Object attachData) {
        actionButtonBooleanMultipleMap.setAllSecondValue(false);

        JComponent viewCache = viewCacheMap.get(mainToolWindowsAction.getName());
        if (viewCache == null) {
            viewCache = mainToolWindowsAction.getViewFactory().get();
            viewCacheMap.put(mainToolWindowsAction.getName(), viewCache);
        }

        actionButtonBooleanMultipleMap.put(mainToolWindowsAction, viewCache, true);
        if (viewCache instanceof ToolComponentPage) {
            ((ToolComponentPage) viewCache).setAttachData(attachData);
        }
        setContent(viewCache);
    }

    private static class BaseAnAction extends AnAction {
        private final MainToolWindowsAction mainToolWindowsAction;

        public BaseAnAction(MainToolWindowsAction mainToolWindowsAction) {
            super(mainToolWindowsAction.getName(), mainToolWindowsAction.getName(), mainToolWindowsAction.getIcon());
            this.mainToolWindowsAction = mainToolWindowsAction;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            super.update(e);
            e.getPresentation().setIcon(mainToolWindowsAction.getIcon());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            AnActionCallback callback = mainToolWindowsAction.getCallback();
            if (callback != null) callback.actionPerformed(e);
        }
    }

    private class ToolAnActionButton extends DynamicIconToggleActionButton {
        private final MainToolWindowsAction mainToolWindowsAction;

        public ToolAnActionButton(MainToolWindowsAction action) {
            super(action::getName, action.getIconFactory());
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
