package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.state.RequestCachePersistentState;
import com.hxl.plugin.springboot.invoke.view.PluginWindowToolBarView;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPContainer extends JPanel implements
        RequestMappingSelectedListener, CommunicationListener, HttpResponseListener {
    private final MainBottomHTTPInvokeView mainBottomHttpInvokeView;
    private final MainBottomHTTPResponseView mainBottomHTTPResponseView;

    public MainBottomHTTPContainer(Project project, PluginWindowToolBarView pluginWindowView) {
        this.mainBottomHttpInvokeView = new MainBottomHTTPInvokeView(project, pluginWindowView);
        this.mainBottomHTTPResponseView = new MainBottomHTTPResponseView(project);

        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(this.mainBottomHttpInvokeView);
        jbSplitter.setSecondComponent(mainBottomHTTPResponseView);
        this.setLayout(new BorderLayout());
        this.add(jbSplitter, BorderLayout.CENTER);
    }


    /**
     * 结果响应，持久化，并通知MainBottomHTTPResponseView
     */
    @Override
    public void onResponse(String requestId, InvokeResponseModel invokeResponseModel) {
        RequestCachePersistentState.getInstance()
                .getState()
                .headerMap.put(requestId, invokeResponseModel.headerToString());
        RequestCachePersistentState.getInstance()
                .getState().responseBodyMap.put(requestId, invokeResponseModel.getData().getBytes());

        mainBottomHttpInvokeView.onResponse(requestId, invokeResponseModel);

        if (mainBottomHttpInvokeView.getSelectRequestMappingId().equalsIgnoreCase(requestId)) {
            mainBottomHTTPResponseView.onResponse(requestId, invokeResponseModel);
        }
    }

    /**
     * 选择controller，展示持久化的数据
     */
    @Override
    public void controllerChooseEvent(RequestMappingModel select) {
        String header = RequestCachePersistentState.getInstance().getState().headerMap.getOrDefault(select.getController().getId(), "");
        byte[] body = RequestCachePersistentState.getInstance().getState().responseBodyMap.getOrDefault(select.getController().getId(), new byte[]{});
        mainBottomHTTPResponseView.setResult(header, new String(body));
        mainBottomHttpInvokeView.controllerChooseEvent(select);
    }

    /**
     * 选择调度器
     */
    @Override
    public void scheduledChooseEvent(SpringScheduledSpringInvokeEndpoint scheduledEndpoint) {
        mainBottomHttpInvokeView.scheduledChooseEvent(scheduledEndpoint);
    }
}
