package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledInvokeBean;
import com.hxl.plugin.springboot.invoke.view.PluginWindowView;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
    public void onResponse(String requestId, List<InvokeResponseModel.Header> headers, String response) {
        mainBottomHTTPResponseView.onResponse(requestId,headers,response);
    }

    @Override
    public void controllerChooseEvent(RequestMappingModel select) {
        mainBottomHttpInvokeView.controllerChooseEvent(select);
    }



    @Override
    public void scheduledChooseEvent(SpringScheduledInvokeBean scheduledEndpoint) {
        mainBottomHttpInvokeView.scheduledChooseEvent(scheduledEndpoint);
    }
}
