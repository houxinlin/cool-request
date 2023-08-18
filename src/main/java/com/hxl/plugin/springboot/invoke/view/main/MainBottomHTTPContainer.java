package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.bean.SpringBootScheduledEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpointPlus;
import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.hxl.plugin.springboot.invoke.view.PluginWindowView;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MainBottomHTTPContainer  extends JPanel implements RequestMappingSelectedListener, CommunicationListener, HttpResponseListener {
    private final MainBottomHTTPInvokeView mainBottomHttpInvokeView ;
    private final MainBottomHTTPResponseView mainBottomHTTPResponseView;
    public MainBottomHTTPContainer(Project project, PluginWindowView pluginWindowView) {
        this.mainBottomHttpInvokeView = new MainBottomHTTPInvokeView(project, pluginWindowView);
        this.mainBottomHTTPResponseView= new MainBottomHTTPResponseView(project);
        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(this.mainBottomHttpInvokeView);
        jbSplitter.setSecondComponent(mainBottomHTTPResponseView);
        this.setLayout(new BorderLayout());
        this.add(jbSplitter, BorderLayout.CENTER);
    }

    @Override
    public void onResponse(String requestId, Map<String, List<String>> headers, byte[] response) {
        mainBottomHTTPResponseView.onResponse(requestId,headers,response);
    }

    @Override
    public void controllerChooseEvent(SpringMvcRequestMappingEndpointPlus select) {
        mainBottomHttpInvokeView.controllerChooseEvent(select);
    }

    @Override
    public void scheduledSelectedEvent(SpringBootScheduledEndpoint scheduledEndpoint) {
        mainBottomHttpInvokeView.scheduledSelectedEvent(scheduledEndpoint);
    }
}
