package com.hxl.plugin.springboot.invoke.view.component;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.action.actions.RequestEnvironmentAnAction;
import com.hxl.plugin.springboot.invoke.action.actions.ImportCurlParamAnAction;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.view.ToolComponentPage;
import com.hxl.plugin.springboot.invoke.view.main.MainBottomHTTPInvokeViewPanel;
import com.hxl.plugin.springboot.invoke.view.main.MainBottomHTTPResponseView;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;

import java.awt.*;

public class MainBottomHTTPContainer extends SimpleToolWindowPanel implements CommunicationListener, ToolComponentPage {
    public static final String PAGE_NAME = "HTTP";
    private final MainBottomHTTPInvokeViewPanel mainBottomHttpInvokeViewPanel;
    private final MainBottomHTTPResponseView mainBottomHTTPResponseView;
    private final Project project;

    public MainBottomHTTPContainer(Project project) {
        super(true);
        this.project = project;
        this.mainBottomHttpInvokeViewPanel = new MainBottomHTTPInvokeViewPanel(project);
        this.mainBottomHTTPResponseView = new MainBottomHTTPResponseView(project);

        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(this.mainBottomHttpInvokeViewPanel);
        jbSplitter.setSecondComponent(mainBottomHTTPResponseView);
        this.setLayout(new BorderLayout());
        this.setContent(jbSplitter);

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(IdeaTopic.DELETE_ALL_DATA, (IdeaTopic.DeleteAllDataEventListener) () -> {
            mainBottomHttpInvokeViewPanel.clearRequestParam();
        });
        connection.subscribe(IdeaTopic.CLEAR_REQUEST_CACHE, new IdeaTopic.ClearRequestCacheEventListener() {
            @Override
            public void onClearEvent(String id) {
                Controller controller = MainBottomHTTPContainer.this.mainBottomHttpInvokeViewPanel.getController();
                if (controller == null) return;
                if (controller.getId().equalsIgnoreCase(id)) {
                }
            }
        });
        DefaultActionGroup menuGroup = new DefaultActionGroup();
        menuGroup.add(new RequestEnvironmentAnAction(project));
        menuGroup.addSeparator();

        menuGroup.add(new ImportCurlParamAnAction(project));
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", menuGroup, false);
        toolbar.setTargetComponent(this);

        setToolbar(toolbar.getComponent());

    }

    @Override
    public void setAttachData(Object object) {
        if (object instanceof MainTopTreeView.RequestMappingNode) {
            mainBottomHttpInvokeViewPanel.controllerChoose(((MainTopTreeView.RequestMappingNode) object).getData());
        }
    }

    @Override
    public String getPageId() {
        return PAGE_NAME;
    }

//    private class EnvironmentJPanel extends JPanel {
//
//        private final ComboBox<RequestEnvironment> environmentJComboBox = new ComboBox<>();
//        private final EmptyEnvironment emptyEnvironment = new EmptyEnvironment();
//
//        public EnvironmentJPanel() {
//            ApplicationManager.getApplication().getMessageBus().connect().subscribe(IdeaTopic.ENVIRONMENT_ADDED, (IdeaTopic.BaseListener) this::loadEnvironmentData);
//            DefaultActionGroup actionGroup = new DefaultActionGroup();
////            actionGroup.add(new EnvironmentAnAction());
//            add(environmentJComboBox);
//            environmentJComboBox.setRenderer(new EnvironmentRenderer());
//            ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("MyToolbar", actionGroup, false);
//            ((ActionToolbar) toolbar.getComponent()).setOrientation(SwingConstants.HORIZONTAL);
//
//            toolbar.setTargetComponent(this);
//            add(toolbar.getComponent());
//            loadEnvironmentData();
//
//            environmentJComboBox.addItemListener(e -> {
//                RequestEnvironment selectedItem = (RequestEnvironment) environmentJComboBox.getSelectedItem();
//                ProjectConfigPersistentComponent.getInstance().projectEnvironmentMap.put(project.getName(), selectedItem.getId());
//
//                project.getMessageBus().syncPublisher(IdeaTopic.ENVIRONMENT_CHANGE).event();
//            });
//            project.putUserData(Constant.MainViewDataProvideKey, new RequestEnvironmentProvide() {
//                @Override
//                public @NotNull RequestEnvironment getSelectRequestEnvironment() {
//                    return ((RequestEnvironment) environmentJComboBox.getSelectedItem());
//                }
//
//                @Override
//                public String applyUrl(Controller requestMappingModel) {
//                    if (getSelectRequestEnvironment() instanceof EmptyEnvironment) {
//                        return StringUtils.joinUrlPath("http://localhost:" + requestMappingModel.getServerPort(),
//                                StringUtils.getFullUrl(requestMappingModel));
//                    }
//                    return StringUtils.joinUrlPath(getSelectRequestEnvironment().getHostAddress(), StringUtils.getFullUrl(requestMappingModel));
//                }
//            });
//        }
//
//        private void loadEnvironmentData() {
//            List<RequestEnvironment> environments = CoolRequestEnvironmentPersistentComponent.getInstance(project).getEnvironments();
//
//            RequestEnvironment[] array = environments.toArray(new RequestEnvironment[]{});
//            ComboBoxModel<RequestEnvironment> comboBoxModel = new DefaultComboBoxModel<>(array);
//            environmentJComboBox.setModel(comboBoxModel);
//            String envId = ProjectConfigPersistentComponent.getInstance().projectEnvironmentMap.getOrDefault(project.getName(), null);
//            int index = -1;
//            if (envId != null) {
//                for (int i = 0; i < environments.size(); i++) {
//                    if (envId.equals(environments.get(i).getId())) index = i;
//                }
//            }
//            environmentJComboBox.addItem(emptyEnvironment);
//            if (index == -1) {
//                environmentJComboBox.setSelectedItem(emptyEnvironment);
//            } else {
//                environmentJComboBox.setSelectedIndex(index);
//            }
//
//        }
//    }
//
//    private static class EnvironmentRenderer extends DefaultListCellRenderer {
//        @Override
//        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//            if (value instanceof com.hxl.plugin.springboot.invoke.bean.RequestEnvironment) {
//                value = ((com.hxl.plugin.springboot.invoke.bean.RequestEnvironment) value).getEnvironmentName();
//            }
//            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        }
//    }

}
