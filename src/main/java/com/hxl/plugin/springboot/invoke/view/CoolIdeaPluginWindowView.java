package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.action.ui.CleanAction;
import com.hxl.plugin.springboot.invoke.action.ui.HelpAction;
import com.hxl.plugin.springboot.invoke.action.ui.RefreshAction;
import com.hxl.plugin.springboot.invoke.action.ui.SettingAction;
import com.hxl.plugin.springboot.invoke.bean.RefreshInvokeRequestBody;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.RefreshInvoke;
import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.listener.EndpointListener;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.listener.ProjectStartupListener;
import com.hxl.plugin.springboot.invoke.model.*;
import com.hxl.plugin.springboot.invoke.net.PluginCommunication;
import com.hxl.plugin.springboot.invoke.utils.*;
import com.hxl.plugin.springboot.invoke.view.dialog.SettingDialog;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.hxl.plugin.springboot.invoke.view.main.MainBottomHTTPContainer;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.List;

/**
 * Main View
 */
public class CoolIdeaPluginWindowView extends SimpleToolWindowPanel implements
        PluginCommunication.MessageCallback, IToolBarViewEvents, ProjectStartupListener {
    private final MainTopTreeView mainTopTreeView;
    private final MainBottomHTTPContainer mainBottomHTTPContainer;
    private final List<CommunicationListener> communicationListenerList = new ArrayList<>();
    private static final Map<String, ServerMessageHandler> messageHandlerMap = new HashMap<>();
    /**
     * 每个项目可以启动N个SpringBoot实例，但是端口会不一样
     */
    private final Map<Integer, ProjectEndpoint> springBootApplicationInstanceData = new HashMap<>();
    private final List<ProjectStartupModel> springBootApplicationStartupModel = new ArrayList<>();

    public void registerCommunicationListener(CommunicationListener communicationListener) {
        this.communicationListenerList.add(communicationListener);
    }

    public CoolIdeaPluginWindowView(Project project) {
        super(true);
        setLayout(new BorderLayout());
        this.mainTopTreeView = new MainTopTreeView(project, this);
        this.mainBottomHTTPContainer = new MainBottomHTTPContainer(project, this);

        communicationListenerList.addAll(List.of(mainTopTreeView, mainBottomHTTPContainer, this));

        messageHandlerMap.put("controller", new ControllerInfoServerMessageHandler());
        messageHandlerMap.put("response_info", new ResponseInfoServerMessageHandler());
        messageHandlerMap.put("clear", new ClearServerMessageHandler());
        messageHandlerMap.put("scheduled", new ScheduledServerMessageHandler());
        messageHandlerMap.put("startup", new ProjectStartupServerMessageHandler());

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new RefreshAction(this));
        group.add(new HelpAction(this));
        group.add(new CleanAction(this));
        group.add(new SettingAction(this));
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", group, false);
        toolbar.setTargetComponent(this);
        setToolbar(toolbar.getComponent());
        initUI();
        try {
            int port = SocketUtils.getSocketUtils().getPort(project);
            System.out.println(port);
            PluginCommunication pluginCommunication = new PluginCommunication(this);
            pluginCommunication.startServer(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initUI() {
        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(mainTopTreeView);
        jbSplitter.setSecondComponent(mainBottomHTTPContainer);
        this.add(jbSplitter, BorderLayout.CENTER);
    }

    @Override
    public void onStartup(ProjectStartupModel model) {
        this.springBootApplicationStartupModel.add(model);
    }

    @Override
    public void openSettingView() {
        SettingDialog.show();
    }

    public void removeIfClosePort() {
        Set<Integer> result = new HashSet<>();
        for (Integer port : springBootApplicationInstanceData.keySet()) {
            try (SocketChannel ignored = SocketChannel.open(new InetSocketAddress(port))) {
            } catch (Exception e) {
                result.add(port);
            }
        }
        result.forEach(springBootApplicationInstanceData::remove);
    }

    public <T extends SpringInvokeEndpoint> int findPort(T invokeBean) {
        for (Integer port : springBootApplicationInstanceData.keySet()) {
            Set<? extends SpringInvokeEndpoint> invokeBeans = new HashSet<>();
            if (invokeBean instanceof SpringMvcRequestMappingSpringInvokeEndpoint) {
                invokeBeans = springBootApplicationInstanceData.get(port).getController();
            } else if (invokeBean instanceof SpringScheduledSpringInvokeEndpoint) {
                invokeBeans = springBootApplicationInstanceData.get(port).getScheduled();
            }
            for (SpringInvokeEndpoint mappingInvokeBean : invokeBeans) {
                if (mappingInvokeBean.getId().equals(invokeBean.getId())) {
                    return port;
                }
            }
        }
        return -1;
    }

    @Override
    public void pluginMessage(String msg) {
        removeIfClosePort();
        System.out.println(msg);
        MessageType messageType = ObjectMappingUtils.readValue(msg, MessageType.class);
        if (!StringUtils.isEmpty(messageType)) {
            messageHandlerMap.getOrDefault(messageType.getType(), msg1 -> {
            }).handler(msg);
        }
    }

    interface ServerMessageHandler {
        void handler(String msg);
    }

    class ProjectStartupServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            ProjectStartupModel projectStartupModel = ObjectMappingUtils.readValue(msg, ProjectStartupModel.class);
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof ProjectStartupListener) {
                    ((ProjectStartupListener) communicationListener).onStartup(projectStartupModel);
                }
            }
        }
    }

    class ControllerInfoServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            RequestMappingModel requestMappingModel = ObjectMappingUtils.readValue(msg, RequestMappingModel.class);
            if (requestMappingModel == null) return;
            ProjectEndpoint projectModuleBean = springBootApplicationInstanceData.computeIfAbsent(requestMappingModel.getPort(), integer -> new ProjectEndpoint());
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof EndpointListener) {
                    ((EndpointListener) communicationListener).onEndpoint(requestMappingModel);
                    projectModuleBean.getController().add(requestMappingModel.getController());
                }
            }
        }
    }

    class ResponseInfoServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            InvokeResponseModel invokeResponseModel = ObjectMappingUtils.readValue(msg, InvokeResponseModel.class);
            if (invokeResponseModel == null) return;
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof HttpResponseListener) {
                    ((HttpResponseListener) communicationListener).onResponse(invokeResponseModel.getId(), invokeResponseModel);
                }
            }
        }
    }

    class ClearServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof EndpointListener) {
                    ((EndpointListener) communicationListener).clear();
                }
            }
        }
    }

    class ScheduledServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            ScheduledModel scheduledModel = ObjectMappingUtils.readValue(msg, ScheduledModel.class);
            if (scheduledModel == null) return;
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof EndpointListener) {
                    ProjectEndpoint projectModuleBean = springBootApplicationInstanceData.computeIfAbsent(scheduledModel.getPort(), integer -> new ProjectEndpoint());
                    ((EndpointListener) communicationListener).onEndpoint(scheduledModel.getScheduledInvokeBeans());
                    projectModuleBean.getScheduled().addAll(scheduledModel.getScheduledInvokeBeans());
                }
            }
        }
    }

    @Override
    public void clearTree() {
        mainTopTreeView.clear();
    }

    @Override
    public void pluginHelp() {

    }

    @Override
    public void refreshTree() {
        if (this.springBootApplicationStartupModel == null) {
            return;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(ProjectUtils.getCurrentProject(), "Refresh") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
                    new RefreshInvoke(projectStartupModel.getPort()).invokeSync(new RefreshInvokeRequestBody());
                }
            }
        });

    }

    public MainBottomHTTPContainer getMainBottomHTTPContainer() {
        return mainBottomHTTPContainer;
    }

    public MainTopTreeView getMainTopTreeView() {
        return mainTopTreeView;
    }

    static class MessageType {
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class ProjectEndpoint {
        private Set<SpringMvcRequestMappingSpringInvokeEndpoint> controller = new HashSet<>();
        private Set<SpringScheduledSpringInvokeEndpoint> scheduled = new HashSet<>();

        public Set<SpringMvcRequestMappingSpringInvokeEndpoint> getController() {
            return controller;
        }

        public void setController(Set<SpringMvcRequestMappingSpringInvokeEndpoint> controller) {
            this.controller = controller;
        }

        public Set<SpringScheduledSpringInvokeEndpoint> getScheduled() {
            return scheduled;
        }

        public void setScheduled(Set<SpringScheduledSpringInvokeEndpoint> scheduled) {
            this.scheduled = scheduled;
        }
    }
}
