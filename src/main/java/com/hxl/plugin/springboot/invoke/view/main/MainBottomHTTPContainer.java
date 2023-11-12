package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.listener.SpringBootChooseEventPolymerize;
import com.hxl.plugin.springboot.invoke.listener.SpringBootComponentSelectedListener;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.state.RequestCachePersistentState;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPContainer extends JPanel implements
        SpringBootChooseEventPolymerize, CommunicationListener, HttpResponseListener {
    private final MainBottomHTTPInvokeView mainBottomHttpInvokeView;
    private final MainBottomHTTPResponseView mainBottomHTTPResponseView;

    public MainBottomHTTPContainer(Project project, CoolIdeaPluginWindowView coolIdeaPluginWindowView) {
        this.mainBottomHttpInvokeView = new MainBottomHTTPInvokeView(project, coolIdeaPluginWindowView);
        this.mainBottomHTTPResponseView = new MainBottomHTTPResponseView(project);

        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(this.mainBottomHttpInvokeView);
        jbSplitter.setSecondComponent(mainBottomHTTPResponseView);
        this.setLayout(new BorderLayout());
        this.add(jbSplitter, BorderLayout.CENTER);

        MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect();
        connection.subscribe(IdeaTopic.HTTP_RESPONSE, (IdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            if (invokeResponseModel != null && !StringUtils.isEmpty(requestId)) {
                MainBottomHTTPContainer.this.onResponse(requestId, invokeResponseModel);
            }
        });
        connection.subscribe(IdeaTopic.SCHEDULED_CHOOSE_EVENT, (IdeaTopic.ScheduledChooseEventListener) this::scheduledChooseEvent);
        connection.subscribe(IdeaTopic.CONTROLLER_CHOOSE_EVENT, (IdeaTopic.ControllerChooseEventListener) this::controllerChooseEvent);

        connection.subscribe(IdeaTopic.DELETE_ALL_DATA, (IdeaTopic.DeleteAllDataEventListener) () -> {
            mainBottomHttpInvokeView.clearRequestParam();
            mainBottomHttpInvokeView.controllerChooseEvent(null);
            mainBottomHttpInvokeView.scheduledChooseEvent(null);
            mainBottomHTTPResponseView.setResult("", "");
        });
        connection.subscribe(IdeaTopic.CLEAR_REQUEST_CACHE, new IdeaTopic.ClearRequestCacheEventListener() {
            @Override
            public void onClearEvent(String id) {
                RequestMappingModel requestMappingModel = MainBottomHTTPContainer.this.mainBottomHttpInvokeView.getRequestMappingModel();
                if (requestMappingModel ==null) return;
                if (requestMappingModel.getController().getId().equalsIgnoreCase(id)) {
                    controllerChooseEvent(requestMappingModel);
                }
                RequestCachePersistentState.getInstance()
                        .getState()
                        .headerMap.put(requestMappingModel.getController().getId(), "");
                RequestCachePersistentState.getInstance()
                        .getState().responseBodyMap.put(requestMappingModel.getController().getId(), new byte[0]);
            }
        });
    }

    /**
     * 结果响应，持久化，并通知MainBottomHTTPResponseView
     * <p>
     * 两种请求方式的响应结果统一走这里
     */
    @Override
    public void onResponse(String requestId, InvokeResponseModel invokeResponseModel) {
        RequestCachePersistentState.getInstance()
                .getState()
                .headerMap.put(requestId, invokeResponseModel.headerToString());
        RequestCachePersistentState.getInstance()
                .getState().responseBodyMap.put(requestId, invokeResponseModel.getData());

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
        //持久化中保存上一次请求的结果--------响应头和响应体
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
