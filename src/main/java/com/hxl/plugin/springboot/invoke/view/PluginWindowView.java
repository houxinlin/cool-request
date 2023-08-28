package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.listener.EndpointListener;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.model.*;
import com.hxl.plugin.springboot.invoke.net.PluginCommunication;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainBottomHTTPContainer;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;

import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.List;

public class PluginWindowView extends JPanel implements PluginCommunication.MessageCallback {
    private final MainTopTreeView mainTopTreeView;
    private final MainBottomHTTPContainer mainBottomHTTPContainer;
    private final List<CommunicationListener> communicationListenerList = new ArrayList<>();
    private static final Map<String, MessageHandler> messageHandlerMap = new HashMap<>();
    private final Map<Integer, ProjectEndpoint> projectRequestBeanMap = new HashMap<>();

    public PluginWindowView(Project project) {
        super(new BorderLayout());
        this.mainTopTreeView = new MainTopTreeView(project, this);
        this.mainBottomHTTPContainer = new MainBottomHTTPContainer(project, this);
        this.mainTopTreeView.registerRequestMappingSelected(mainBottomHTTPContainer);
        communicationListenerList.add(mainTopTreeView);
        communicationListenerList.add(mainBottomHTTPContainer);

        messageHandlerMap.put("controller", new ControllerInfoController());
        messageHandlerMap.put("response_info", new ResponseInfoMessageHandler());
        messageHandlerMap.put("clear", new ClearMessageHandler());
        messageHandlerMap.put("scheduled", new ScheduledMessageHandler());

        initUI();
    }

    public void initUI() {
        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(mainTopTreeView);
        jbSplitter.setSecondComponent(mainBottomHTTPContainer);
        this.add(jbSplitter, BorderLayout.CENTER);
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

    public <T extends InvokeBean> int findPort(T invokeBean) {
        for (Integer port : projectRequestBeanMap.keySet()) {
            Set<? extends InvokeBean> invokeBeans = new HashSet<>();
            if (invokeBean instanceof SpringMvcRequestMappingInvokeBean) {
                invokeBeans = projectRequestBeanMap.get(port).getController();
            } else if (invokeBean instanceof SpringScheduledInvokeBean) {
                invokeBeans = projectRequestBeanMap.get(port).getScheduled();
            }
            for (InvokeBean mappingInvokeBean : invokeBeans) {
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
        MessageType messageType = ObjectMappingUtils.readValue(msg, MessageType.class);
        if (!StringUtils.isEmpty(messageType)) {
            messageHandlerMap.getOrDefault(messageType.getType(), msg1 -> {
            }).handler(msg);
        }
    }

    interface MessageHandler {
        void handler(String msg);
    }

    class ControllerInfoController implements MessageHandler {
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
            if (invokeResponseModel==null)return;
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof HttpResponseListener) {

                    ((HttpResponseListener) communicationListener).onResponse(invokeResponseModel.getId(), invokeResponseModel.getHeader(), invokeResponseModel.getData());
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
            if (scheduledModel==null) return;
            for (CommunicationListener communicationListener : communicationListenerList) {
                if (communicationListener instanceof EndpointListener) {
                    ProjectEndpoint projectModuleBean = projectRequestBeanMap.computeIfAbsent(scheduledModel.getPort(), integer -> new ProjectEndpoint());
                    ((EndpointListener) communicationListener).onEndpoint(scheduledModel.getScheduledInvokeBeans());
                    projectModuleBean.getScheduled().addAll(scheduledModel.getScheduledInvokeBeans());
                }
            }
        }
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
        private Set<SpringMvcRequestMappingInvokeBean> controller = new HashSet<>();
        private Set<SpringScheduledInvokeBean> scheduled = new HashSet<>();

        public Set<SpringMvcRequestMappingInvokeBean> getController() {
            return controller;
        }

        public void setController(Set<SpringMvcRequestMappingInvokeBean> controller) {
            this.controller = controller;
        }

        public Set<SpringScheduledInvokeBean> getScheduled() {
            return scheduled;
        }

        public void setScheduled(Set<SpringScheduledInvokeBean> scheduled) {
            this.scheduled = scheduled;
        }
    }
}
