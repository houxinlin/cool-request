package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.RequestMappingWrapper;
import com.hxl.plugin.springboot.invoke.listener.CommunicationListener;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.listener.SpringBootChooseEventPolymerize;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.utils.service.CacheStorageService;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPContainer extends JPanel implements
        SpringBootChooseEventPolymerize, CommunicationListener, HttpResponseListener {
    private final MainBottomHTTPInvokeViewPanel mainBottomHttpInvokeViewPanel;
    private final MainBottomHTTPResponseView mainBottomHTTPResponseView;

    public MainBottomHTTPContainer(Project project, CoolIdeaPluginWindowView coolIdeaPluginWindowView) {
        this.mainBottomHttpInvokeViewPanel = new MainBottomHTTPInvokeViewPanel(project, coolIdeaPluginWindowView);
        this.mainBottomHTTPResponseView = new MainBottomHTTPResponseView(project);

        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(this.mainBottomHttpInvokeViewPanel);
        jbSplitter.setSecondComponent(mainBottomHTTPResponseView);
        this.setLayout(new BorderLayout());
        this.add(jbSplitter, BorderLayout.CENTER);

        MessageBusConnection connection = project.getMessageBus().connect();

        connection.subscribe(IdeaTopic.HTTP_RESPONSE, (IdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            if (invokeResponseModel != null && !StringUtils.isEmpty(requestId)) {
                MainBottomHTTPContainer.this.onHttpResponseEvent(requestId, invokeResponseModel);
            }
        });
        connection.subscribe(IdeaTopic.SCHEDULED_CHOOSE_EVENT, (IdeaTopic.ScheduledChooseEventListener) this::scheduledChooseEvent);
        connection.subscribe(IdeaTopic.CONTROLLER_CHOOSE_EVENT, new IdeaTopic.ControllerChooseEventListener() {
            @Override
            public void onChooseEvent(RequestMappingWrapper requestMappingWrapper) {
                SwingUtilities.invokeLater(() -> controllerChooseEvent(requestMappingWrapper));
            }

            @Override
            public void refreshEvent(RequestMappingModel requestMappingModel) {
                RequestMappingWrapper requestMappingWrapper = mainBottomHttpInvokeViewPanel.getRequestMappingWrapper();
                if (requestMappingWrapper == null) return;
                for (SpringMvcRequestMappingSpringInvokeEndpoint springMvcRequestMappingSpringInvokeEndpoint : requestMappingModel.getController()) {
                    if (springMvcRequestMappingSpringInvokeEndpoint.getId().equalsIgnoreCase(requestMappingWrapper.getController().getId())) {
                        SwingUtilities.invokeLater(() ->
                                controllerChooseEvent(new RequestMappingWrapper(springMvcRequestMappingSpringInvokeEndpoint,
                                        requestMappingModel.getContextPath(), requestMappingModel.getServerPort(),requestMappingModel.getPort())));
                    }
                }
            }
        });

        connection.subscribe(IdeaTopic.DELETE_ALL_DATA, (IdeaTopic.DeleteAllDataEventListener) () -> {
            mainBottomHttpInvokeViewPanel.clearRequestParam();
            mainBottomHttpInvokeViewPanel.controllerChooseEvent(null);
            mainBottomHttpInvokeViewPanel.scheduledChooseEvent(null, -1);
        });
        connection.subscribe(IdeaTopic.CLEAR_REQUEST_CACHE, new IdeaTopic.ClearRequestCacheEventListener() {
            @Override
            public void onClearEvent(String id) {
                RequestMappingWrapper requestMappingModel = MainBottomHTTPContainer.this.mainBottomHttpInvokeViewPanel.getRequestMappingWrapper();
                if (requestMappingModel == null) return;
                if (requestMappingModel.getController().getId().equalsIgnoreCase(id)) {
                    controllerChooseEvent(requestMappingModel);
                }
//                RequestCachePersistentState.getInstance()
//                        .getState()
//                        .headerMap.put(requestMappingModel.getController().getId(), "");
//                RequestCachePersistentState.getInstance()
//                        .getState().responseBodyMap.put(requestMappingModel.getController().getId(), new byte[0]);
            }
        });
    }

    /**
     * 结果响应，持久化，并通知MainBottomHTTPResponseView
     * <p>
     * 两种请求方式的响应结果统一走这里
     */
    @Override
    public void onHttpResponseEvent(String requestId, InvokeResponseModel invokeResponseModel) {
        CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
        service.storageResponseCache(requestId, invokeResponseModel);

        mainBottomHttpInvokeViewPanel.onHttpResponseEvent(requestId, invokeResponseModel);

        if (mainBottomHttpInvokeViewPanel.getSelectRequestMappingId().equalsIgnoreCase(requestId)) {
            mainBottomHTTPResponseView.onHttpResponseEvent(requestId, invokeResponseModel);
        }
    }

    /**
     * 选择controller，展示持久化的数据
     */
    @Override
    public void controllerChooseEvent(RequestMappingWrapper requestMappingWrapper) {
        //持久化中保存上一次请求的结果--------响应头和响应体
        if (requestMappingWrapper == null) return;
        CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
        String requestId = requestMappingWrapper.getController().getId();
        InvokeResponseModel invokeResponseModel = service.loadResponseCache(requestId);
        mainBottomHTTPResponseView.onHttpResponseEvent(requestId, invokeResponseModel);
        mainBottomHttpInvokeViewPanel.controllerChooseEvent(requestMappingWrapper);
    }

    /**
     * 选择调度器
     */
    public void scheduledChooseEvent(SpringScheduledSpringInvokeEndpoint scheduledEndpoint, int port) {
        mainBottomHttpInvokeViewPanel.scheduledChooseEvent(scheduledEndpoint, port);
    }
}
