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
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

public class PluginWindowToolBarView extends SimpleToolWindowPanel implements
        PluginCommunication.MessageCallback, IToolBarViewEvents, ProjectStartupListener {
    private final MainTopTreeView mainTopTreeView;
    private final MainBottomHTTPContainer mainBottomHTTPContainer;
    private final List<CommunicationListener> communicationListenerList = new ArrayList<>();
    private static final Map<String, MessageHandler> messageHandlerMap = new HashMap<>();
    private final Map<Integer, ProjectEndpoint> projectRequestBeanMap = new HashMap<>();
    private final PluginCommunication pluginCommunication = new PluginCommunication(this);
    private ProjectStartupModel projectStartupModel;

    public void registerCommunicationListener(CommunicationListener communicationListener) {
        this.communicationListenerList.add(communicationListener);
    }

    @Override
    public void onStartup(ProjectStartupModel model) {
        this.projectStartupModel = model;
    }

    public PluginWindowToolBarView(Project project) {
        super(true);
        setLayout(new BorderLayout());
        this.mainTopTreeView = new MainTopTreeView(project, this);
        this.mainBottomHTTPContainer = new MainBottomHTTPContainer(project, this);
        this.mainTopTreeView.registerRequestMappingSelected(mainBottomHTTPContainer);

        communicationListenerList.add(mainTopTreeView);
        communicationListenerList.add(mainBottomHTTPContainer);
        communicationListenerList.add(this);

        messageHandlerMap.put("controller", new ControllerInfoMessageHandler());
        messageHandlerMap.put("response_info", new ResponseInfoMessageHandler());
        messageHandlerMap.put("clear", new ClearMessageHandler());
        messageHandlerMap.put("scheduled", new ScheduledMessageHandler());
        messageHandlerMap.put("startup", new ProjectStartupMessageHandler());

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
    public void openSettingView() {
        SettingDialog.show();
    }

    public void removeIfClosePort() {
        Set<Integer> result = new HashSet<>();
        for (Integer port : projectRequestBeanMap.keySet()) {
            try (SocketChannel ignored = SocketChannel.open(new InetSocketAddress(port))) {
            } catch (Exception e) {
                result.add(port);
            }
        }
        result.forEach(projectRequestBeanMap::remove);
    }

    public <T extends SpringInvokeEndpoint> int findPort(T invokeBean) {
        for (Integer port : projectRequestBeanMap.keySet()) {
            Set<? extends SpringInvokeEndpoint> invokeBeans = new HashSet<>();
            if (invokeBean instanceof SpringMvcRequestMappingSpringInvokeEndpoint) {
                invokeBeans = projectRequestBeanMap.get(port).getController();
            } else if (invokeBean instanceof SpringScheduledSpringInvokeEndpoint) {
                invokeBeans = projectRequestBeanMap.get(port).getScheduled();
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

    interface MessageHandler {
        void handler(String msg);
    }

    class ProjectStartupMessageHandler implements MessageHandler {
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

    class ControllerInfoMessageHandler implements MessageHandler {
        @Override
        public void handler(String msg) {
            RequestMappingModel requestMappingModel = ObjectMappingUtils.readValue(msg, RequestMappingModel.class);
            if (requestMappingModel == null) return;
            ProjectEndpoint projectModuleBean = projectRequestBeanMap.computeIfAbsent(requestMappingModel.getPort(), integer -> new ProjectEndpoint());
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof EndpointListener) {
                    ((EndpointListener) communicationListener).onEndpoint(requestMappingModel);
                    projectModuleBean.getController().add(requestMappingModel.getController());
                }
            }
        }
    }

    class ResponseInfoMessageHandler implements MessageHandler {
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

    class ClearMessageHandler implements MessageHandler {
        @Override
        public void handler(String msg) {
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof EndpointListener) {
                    ((EndpointListener) communicationListener).clear();
                }
            }
        }
    }

    class ScheduledMessageHandler implements MessageHandler {
        @Override
        public void handler(String msg) {
            ScheduledModel scheduledModel = ObjectMappingUtils.readValue(msg, ScheduledModel.class);
            if (scheduledModel == null) return;
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof EndpointListener) {
                    ProjectEndpoint projectModuleBean = projectRequestBeanMap.computeIfAbsent(scheduledModel.getPort(), integer -> new ProjectEndpoint());
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
        if (this.projectStartupModel == null) {
            return;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(ProjectUtils.getCurrentProject(), "refresh") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                InvokeResult invokeResult = new RefreshInvoke(projectStartupModel.getPort()).invokeSync(new RefreshInvokeRequestBody());
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
