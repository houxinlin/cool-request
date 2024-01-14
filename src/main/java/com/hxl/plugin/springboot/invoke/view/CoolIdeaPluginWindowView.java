package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.action.actions.*;
import com.hxl.plugin.springboot.invoke.bean.EmptyEnvironment;
import com.hxl.plugin.springboot.invoke.bean.RequestEnvironment;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.state.CoolRequestEnvironmentPersistentComponent;
import com.hxl.plugin.springboot.invoke.state.project.ProjectConfigPersistentComponent;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.utils.WebBrowseUtils;
import com.hxl.plugin.springboot.invoke.view.dialog.SettingDialog;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.hxl.plugin.springboot.invoke.view.main.MainBottomHTTPContainer;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.hxl.plugin.springboot.invoke.view.main.MainViewDataProvide;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Main View
 */
public class CoolIdeaPluginWindowView extends SimpleToolWindowPanel implements IToolBarViewEvents {
    private final MainTopTreeView mainTopTreeView;
    private final MainBottomHTTPContainer mainBottomHTTPContainer;

    private final JBSplitter jbSplitter = new JBSplitter(true, "", 0.35f);
    private final Project project;
    private final DefaultActionGroup menuGroup = new DefaultActionGroup();
    private boolean showUpdateMenu = false;

    public CoolIdeaPluginWindowView(Project project) {
        super(true);
        this.project = project;
        setLayout(new BorderLayout());

        this.mainTopTreeView = new MainTopTreeView(project, this);
        this.mainBottomHTTPContainer = new MainBottomHTTPContainer(project, this);

        initUI();
    }

    private void initToolBar() {

        menuGroup.add(new RefreshAction(project, this));
        menuGroup.add(new CleanAction(project, this));
        menuGroup.addSeparator();

        menuGroup.add(new CollapseAction(project));
        menuGroup.add(new ExpandAction(project));
        menuGroup.add(new FindAction(project));
        menuGroup.addSeparator();

        menuGroup.add(new FloatWindowsAnAction(project));
        menuGroup.add(new ChangeMainLayoutAnAction(project));
        menuGroup.addSeparator();

        menuGroup.add(new BugAction(project));
        menuGroup.add(new HelpAction(project, this));
        menuGroup.add(new ContactAnAction(project));
        menuGroup.addSeparator();

        menuGroup.add(new SettingAction(project, this));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", menuGroup, false);

        JPanel topBarJPanel = new JPanel(new BorderLayout());
        toolbar.setTargetComponent(topBarJPanel);
        ((ActionToolbar) toolbar.getComponent()).setOrientation(myVertical ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL);

        topBarJPanel.add(toolbar.getComponent(), BorderLayout.WEST);
        topBarJPanel.add(new EnvironmentJPanel(), BorderLayout.EAST);
        setToolbar(topBarJPanel);

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
            if (childActionsOrStub.getTemplatePresentation().getText().equalsIgnoreCase(title)) {
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

    public void initUI() {
        initToolBar();
        project.getMessageBus().connect().subscribe(IdeaTopic.CHANGE_LAYOUT,
                (IdeaTopic.BaseListener) () -> {
                    boolean orientation = jbSplitter.getOrientation();
                    jbSplitter.setOrientation(!orientation);
                });

        jbSplitter.setFirstComponent(mainTopTreeView);
        jbSplitter.setSecondComponent(mainBottomHTTPContainer);
        this.add(jbSplitter, BorderLayout.CENTER);
    }

    @Override
    public void openSettingView() {
        SettingDialog.show(project);
    }

    @Override
    public void clearAllData() {
        mainTopTreeView.getProject().getMessageBus().syncPublisher(IdeaTopic.DELETE_ALL_DATA).onDelete();

    }

    @Override
    public void pluginHelp() {
        WebBrowseUtils.browse("http://plugin.houxinlin.com");
    }

    @Override
    public void refreshTree() {
//        List<ProjectStartupModel> springBootApplicationStartupModel = userProjectManager.getSpringBootApplicationStartupModel();
//        //删除可以通信的端口
//        Set<Integer> ports = new HashSet<>();
//        for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
//            if (SocketUtils.canConnection(projectStartupModel.getPort())) {
//                ports.add(projectStartupModel.getProjectPort());
//            }
//        }
//        if (!ports.isEmpty()) {
//            this.clearTree();
//        }
//        userProjectManager.projectEndpointRefresh();
    }

    public MainBottomHTTPContainer getMainBottomHTTPContainer() {
        return mainBottomHTTPContainer;
    }

    public MainTopTreeView getMainTopTreeView() {
        return mainTopTreeView;
    }

    private static class EnvironmentRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof com.hxl.plugin.springboot.invoke.bean.RequestEnvironment) {
                value = ((com.hxl.plugin.springboot.invoke.bean.RequestEnvironment) value).getEnvironmentName();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    private class EnvironmentJPanel extends JPanel {

        private final ComboBox<RequestEnvironment> environmentJComboBox = new ComboBox<>();
        private final EmptyEnvironment emptyEnvironment = new EmptyEnvironment();

        public EnvironmentJPanel() {
            ApplicationManager.getApplication().getMessageBus().connect().subscribe(IdeaTopic.ENVIRONMENT_ADDED, (IdeaTopic.BaseListener) this::loadEnvironmentData);
            DefaultActionGroup actionGroup = new DefaultActionGroup();
            actionGroup.add(new EnvironmentAnAction());
            add(environmentJComboBox);
            environmentJComboBox.setRenderer(new EnvironmentRenderer());
            ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("MyToolbar", actionGroup, false);
            ((ActionToolbar) toolbar.getComponent()).setOrientation(SwingConstants.HORIZONTAL);

            toolbar.setTargetComponent(this);
            add(toolbar.getComponent());
            loadEnvironmentData();

            environmentJComboBox.addItemListener(e -> {
                RequestEnvironment selectedItem = (RequestEnvironment) environmentJComboBox.getSelectedItem();
                ProjectConfigPersistentComponent.getInstance().projectEnvironmentMap.put(project.getName(), selectedItem.getId());

                project.getMessageBus().syncPublisher(IdeaTopic.ENVIRONMENT_CHANGE).event();
            });
            project.putUserData(Constant.MainViewDataProvideKey, new MainViewDataProvide() {
                @Override
                public @NotNull RequestEnvironment getSelectRequestEnvironment() {
                    return ((RequestEnvironment) environmentJComboBox.getSelectedItem());
                }

                @Override
                public String applyUrl(Controller requestMappingModel) {
                    if (getSelectRequestEnvironment() instanceof EmptyEnvironment) {
                        return StringUtils.joinUrlPath("http://localhost:" + requestMappingModel.getServerPort(),
                                StringUtils.getFullUrl(requestMappingModel));
                    }
                    return StringUtils.joinUrlPath(getSelectRequestEnvironment().getHostAddress(), StringUtils.getFullUrl(requestMappingModel));
                }
            });
        }

        private void loadEnvironmentData() {
            List<RequestEnvironment> environments = CoolRequestEnvironmentPersistentComponent.getInstance().environments;

            RequestEnvironment[] array = environments.toArray(new RequestEnvironment[]{});
            ComboBoxModel<RequestEnvironment> comboBoxModel = new DefaultComboBoxModel<>(array);
            environmentJComboBox.setModel(comboBoxModel);
            String envId = ProjectConfigPersistentComponent.getInstance().projectEnvironmentMap.getOrDefault(project.getName(), null);
            int index = -1;
            if (envId != null) {
                for (int i = 0; i < environments.size(); i++) {
                    if (envId.equals(environments.get(i).getId())) index = i;
                }
            }
            environmentJComboBox.addItem(emptyEnvironment);
            if (index == -1) {
                environmentJComboBox.setSelectedItem(emptyEnvironment);
            } else {
                environmentJComboBox.setSelectedIndex(index);
            }

        }
    }
}
