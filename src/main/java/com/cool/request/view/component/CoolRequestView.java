/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestView.java is part of Cool Request
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

package com.cool.request.view.component;

import com.cool.request.action.actions.*;
import com.cool.request.common.bean.components.BasicComponent;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.service.ProjectViewSingleton;
import com.cool.request.common.state.MarkPersistent;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.components.ComponentType;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.components.http.Controller;
import com.cool.request.scan.CoolRequestScan;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.ToolComponentPage;
import com.cool.request.view.events.IToolBarViewEvents;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.main.MainTopTreeViewManager;
import com.cool.request.view.tool.CoolRequest;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Main View
 */
public class CoolRequestView extends SimpleToolWindowPanel implements
        IToolBarViewEvents, ToolComponentPage, ShowMarkNodeAnAction.MakeSelectedListener {
    public static final String PAGE_NAME = "Api";
    private final DefaultActionGroup menuGroup = new DefaultActionGroup();
    private final JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
    private final MainTopTreeView mainTopTreeView;
    private MainBottomHTTPContainer mainBottomHTTPContainer;
    private final Project project;
    private boolean showUpdateMenu = false;
    private boolean markSelected;
    private final MainTopTreeViewManager mainTopTreeViewManager;

    public static CoolRequestView getInstance(Project project) {
        return ProjectViewSingleton.getInstance(project).createAndCoolRequestView();
    }

    public MainTopTreeViewManager getMainTopTreeViewManager() {
        return mainTopTreeViewManager;
    }

    public CoolRequestView(Project project) {
        super(true);
        this.project = project;
        this.mainTopTreeView = new MainTopTreeView(project, this);
        this.mainTopTreeViewManager = new MainTopTreeViewManager(mainTopTreeView, project);

        ProviderManager.registerProvider(MainTopTreeViewManager.class, CoolRequestConfigConstant.MainTopTreeViewManagerKey, mainTopTreeViewManager, project);
        setLayout(new BorderLayout());

        SettingsState state = SettingPersistentState.getInstance().getState();
        if (state.mergeApiAndRequest) {
            this.mainBottomHTTPContainer = ProjectViewSingleton.getInstance(project).createAndGetMainBottomHTTPContainer();
        }
        MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();
        connect.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, (CoolRequestIdeaTopic.BaseListener) () -> {
            SettingsState state1 = SettingPersistentState.getInstance().getState();
            if (state1.mergeApiAndRequest && jbSplitter.getSecondComponent() == null) {
                if (mainBottomHTTPContainer == null) {
                    mainBottomHTTPContainer = ProjectViewSingleton.getInstance(project).createAndGetMainBottomHTTPContainer();
                }
                jbSplitter.setSecondComponent(mainBottomHTTPContainer);
            }
            if (!state1.mergeApiAndRequest) {
                jbSplitter.setSecondComponent(null);
            }
        });
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), connect);
        initUI();
        // 刷新视图
        DumbService.getInstance(project).smartInvokeLater(() -> {
            CoolRequestScan.staticScan(project, null);
        });
        CoolRequest.getInstance(project).attachView(this);
    }

    public void initUI() {
        initToolBar();
        jbSplitter.setFirstComponent(mainTopTreeView);
        if (mainBottomHTTPContainer != null) {
            jbSplitter.setSecondComponent(mainBottomHTTPContainer);
            mainBottomHTTPContainer.invalidate();
            mainBottomHTTPContainer.updateUI();
        }
        this.add(jbSplitter, BorderLayout.CENTER);
        jbSplitter.invalidate();
        jbSplitter.updateUI();

    }

    private void initToolBar() {
        menuGroup.add(new ShowMarkNodeAnAction(this));
        menuGroup.addSeparator();

        menuGroup.add(new RefreshAction(project, this));
        menuGroup.addSeparator();

        menuGroup.add(new CollapseAction(project));
        menuGroup.add(new ExpandAction(project));
        menuGroup.add(new FindAction(project));
        menuGroup.addSeparator();

        menuGroup.add(new FloatWindowsAnAction(project));

        menuGroup.addSeparator();

        menuGroup.add(new HelpAction(project, this));
        menuGroup.addSeparator();
        menuGroup.add(new ContactAnAction(project));
        menuGroup.add(new GithubAnAction());
        menuGroup.add(new CoolRequestAnAction(project));
        menuGroup.add(new PayAnAction(project));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("ApiToolPage@bar", menuGroup, false);
        toolbar.setTargetComponent(this);
        setToolbar(toolbar.getComponent());

    }

    public void removeAllDynamicAnActions() {
        AnAction[] childActionsOrStubs = menuGroup.getChildActionsOrStubs();
        for (AnAction childActionsOrStub : childActionsOrStubs) {
            if (childActionsOrStub instanceof DynamicUrlAnAction) {
                menuGroup.remove(childActionsOrStub);
            }
        }
    }

    public void addNewDynamicAnAction(String title, String url, ImageIcon imageIcon) {
        AnAction[] childActionsOrStubs = menuGroup.getChildActionsOrStubs();
        for (AnAction childActionsOrStub : childActionsOrStubs) {
            String text = childActionsOrStub.getTemplatePresentation().getText();
            if (StringUtils.isEmpty(text)) continue;
            if (text.equalsIgnoreCase(title)) {
                return;
            }
        }
        SwingUtilities.invokeLater(() -> menuGroup.add(new DynamicUrlAnAction(title, imageIcon, url)));
    }

    public void showUpdateMenu() {
        if (showUpdateMenu) {
            return;
        }
        showUpdateMenu = true;
        menuGroup.add(new UpdateAction(this));
    }


    public MainBottomHTTPContainer getMainBottomHTTPContainer() {
        return mainBottomHTTPContainer;
    }

    public MainTopTreeView getMainTopTreeView() {
        return mainTopTreeView;
    }


    @Override
    public void setAttachData(Object object) {
        if (object == null) return;

    }

    @Override
    public void setMarkSelected(@NotNull AnActionEvent e, boolean state) {
        markSelected = state;
        ProviderManager.findAndConsumerProvider(MainTopTreeViewManager.class, project, MainTopTreeViewManager::clearData);

        UserProjectManager userProjectManager = UserProjectManager.getInstance(project);
        if (userProjectManager == null) return;
        Map<ComponentType, List<Component>> projectComponents = userProjectManager.getProjectComponents();

        projectComponents.forEach((componentType, components) -> {
            List<? extends Controller> component =
                    convert(projectComponents.getOrDefault(componentType, new ArrayList<>()), Controller.class, state);
            if (markSelected) {
                MarkPersistent markPersistent = MarkPersistent.getInstance(project);
                Set<String> setNotInList = new HashSet<>(markPersistent.getState().getMarkComponentMap().computeIfAbsent(componentType, (v) -> new HashSet<>()));
                component.stream()
                        .map((Function<Controller, String>) BasicComponent::getId)
                        .collect(Collectors.toList())
                        .forEach(setNotInList::remove);
                markPersistent.getState().getMarkComponentMap().computeIfAbsent(componentType, (v) -> new HashSet<>()).removeIf(setNotInList::contains);
            }
            getMainTopTreeViewManager()
                    .addComponent(component, componentType);
        });

        getMainTopTreeViewManager().addCustomController();

    }

    @Override
    public boolean canRefresh() {
        return !markSelected;
    }

    public boolean isMarkSelected() {
        return markSelected;
    }

    private <T> List<? extends T> convert(List<? extends Component> components,
                                          Class<T> target, boolean markFilter) {

        List<T> result = new ArrayList<>();
        MarkPersistent markPersistent = MarkPersistent.getInstance(project);
        for (Component component : components) {
            if (target.isAssignableFrom(component.getClass())) {
                if (!markFilter) {
                    result.add((T) component);
                } else {
                    if (markPersistent.in(component)) {
                        result.add((T) component);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getPageId() {
        return PAGE_NAME;
    }
}
