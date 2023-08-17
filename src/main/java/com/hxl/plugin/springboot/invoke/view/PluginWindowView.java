package com.hxl.plugin.springboot.invoke.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxl.plugin.springboot.invoke.bean.InvokeBean;
import com.hxl.plugin.springboot.invoke.bean.ProjectRequestBean;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringBootScheduledEndpoint;
import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.listener.EndpointListener;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.net.PluginCommunication;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
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
    private final Project project;
    private final MainTopTreeView mainTopTreeView;
//    private final MainBottomHttpInvokeView mainBottomHttpInvokeView;
    private MainBottomHTTPContainer mainBottomHTTPContainer;
    private final List<CommunicationListener> communicationListenerList = new ArrayList<>();

    public PluginWindowView(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.mainTopTreeView = new MainTopTreeView(this);

        this.mainBottomHTTPContainer= new MainBottomHTTPContainer(project,this);
        this.mainTopTreeView.registerRequestMappingSelected(mainBottomHTTPContainer);

        communicationListenerList.add(mainTopTreeView);
        communicationListenerList.add(mainBottomHTTPContainer);

        initUI();
    }

    public void initUI() {
        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(mainTopTreeView);
        jbSplitter.setSecondComponent(mainBottomHTTPContainer);
        this.add(jbSplitter, BorderLayout.CENTER);
    }

    private static final String BEAN_INFO = "bean_info";
    private static final String RESPONSE_INFO = "response_info";
    private final Map<Integer, ProjectEndpoint> projectRequestBeanMap = new HashMap<>();

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
            if (invokeBean instanceof SpringMvcRequestMappingEndpoint) {
                invokeBeans = projectRequestBeanMap.get(port).getController();
            } else if (invokeBean instanceof SpringBootScheduledEndpoint) {
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
        try {
            removeIfClosePort();
            ProjectRequestBean requestBean = ObjectMappingUtils.getInstance().readValue(msg, ProjectRequestBean.class);
            if (BEAN_INFO.equalsIgnoreCase(requestBean.getType())) {
                ProjectEndpoint projectModuleBean = projectRequestBeanMap.computeIfAbsent(requestBean.getPort(), integer -> new ProjectEndpoint());
                if (requestBean.getScheduled() != null) {
                    projectModuleBean.getScheduled().addAll(requestBean.getScheduled());
                }
                if (requestBean.getController() != null) {
                    projectModuleBean.getController().addAll(requestBean.getController());
                }
                for (CommunicationListener communicationListener : communicationListenerList) {
                    if (communicationListener instanceof EndpointListener) {
                        ((EndpointListener) communicationListener).onEndpoint(requestBean.getServerPort(),
                                requestBean.getContextPath(), projectModuleBean.getController(), projectModuleBean.getScheduled());
                    }
                }
                return;
            }
            if (RESPONSE_INFO.equalsIgnoreCase(requestBean.getType())) {
                for (CommunicationListener communicationListener : communicationListenerList) {
                    if (communicationListener instanceof HttpResponseListener) {
                        ((HttpResponseListener) communicationListener).onResponse(requestBean.getId(), requestBean.getResponseHeaders(), requestBean.getResponse().getBytes());
                    }
                }
                return;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static class ProjectEndpoint {
        private Set<SpringMvcRequestMappingEndpoint> controller = new HashSet<>();
        private Set<SpringBootScheduledEndpoint> scheduled = new HashSet<>();

        public Set<SpringMvcRequestMappingEndpoint> getController() {
            return controller;
        }

        public void setController(Set<SpringMvcRequestMappingEndpoint> controller) {
            this.controller = controller;
        }

        public Set<SpringBootScheduledEndpoint> getScheduled() {
            return scheduled;
        }

        public void setScheduled(Set<SpringBootScheduledEndpoint> scheduled) {
            this.scheduled = scheduled;
        }
    }
}
