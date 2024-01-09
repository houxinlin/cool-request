package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.RequestMappingWrapper;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.ScheduledInvoke;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.listener.SpringBootChooseEventPolymerize;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.net.RequestManager;
import com.hxl.plugin.springboot.invoke.script.JavaCodeEngine;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;
import com.hxl.plugin.springboot.invoke.utils.*;
import com.hxl.plugin.springboot.invoke.view.BottomScheduledUI;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPInvokeViewPanel extends JPanel implements
        SpringBootChooseEventPolymerize,
        BottomScheduledUI.InvokeClick,
        HttpResponseListener {
    private final Project project;
    private final CoolIdeaPluginWindowView coolIdeaPluginWindowView;
    private final MainBottomHTTPInvokeRequestParamManagerPanel httpRequestParamPanel;
    private final BottomScheduledUI bottomScheduledUI;
    private RequestMappingWrapper requestMappingWrapper;
    private Controller currentSelectController;
    private SpringScheduledSpringInvokeEndpointWrapper selectSpringBootScheduledEndpoint;
    private final CardLayout cardLayout = new CardLayout();
    private final RequestManager requestManager;
    private final UserProjectManager userProjectManager;

    public MainBottomHTTPInvokeViewPanel(@NotNull Project project, CoolIdeaPluginWindowView coolIdeaPluginWindowView) {
        this.coolIdeaPluginWindowView = coolIdeaPluginWindowView;
        this.project = project;
        this.userProjectManager = this.project.getUserData(Constant.UserProjectManagerKey);
        this.httpRequestParamPanel = new MainBottomHTTPInvokeRequestParamManagerPanel(project, this);
        this.requestManager = new RequestManager(httpRequestParamPanel.getRequestParamManager(), project, this.userProjectManager);
        this.bottomScheduledUI = new BottomScheduledUI(this);
        this.setLayout(cardLayout);
        this.add(bottomScheduledUI, BottomScheduledUI.class.getName());
        this.add(httpRequestParamPanel, MainBottomHTTPInvokeRequestParamManagerPanel.class.getName());
        switchPage(Panel.CONTROLLER);
        httpRequestParamPanel.setSendRequestClickEvent(e -> requestManager.sendRequest(httpRequestParamPanel.getCurrentRequestMappingModel()));
        project.getMessageBus().connect().subscribe(IdeaTopic.DELETE_ALL_DATA,
                (IdeaTopic.DeleteAllDataEventListener) requestManager::removeAllData);

        project.getMessageBus().connect().subscribe(IdeaTopic.CONTROLLER_CHOOSE_EVENT, new IdeaTopic.ControllerChooseEventListener() {
            @Override
            public void onChooseEvent(RequestMappingWrapper requestId) {

            }

            @Override
            public void onChooseEvent(Controller requestId) {

            }

            @Override
            public void refreshEvent(RequestMappingModel requestMappingModel) {

            }
        });
    }

    public String getSelectRequestMappingId() {
        if (this.requestMappingWrapper != null) return this.requestMappingWrapper.getController().getId();
        return "";
    }

    public boolean canEnabledSendButton(String id) {
        return requestManager.canEnabledSendButton(id);
    }

    @Override
    public void onScheduledInvokeClick() {
        ScheduledInvoke.InvokeData invokeData = new ScheduledInvoke.InvokeData(this.selectSpringBootScheduledEndpoint.getSpringScheduledSpringInvokeEndpoint().getId());
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Invoke") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                InvokeResult invokeResult = new ScheduledInvoke(MainBottomHTTPInvokeViewPanel.this.selectSpringBootScheduledEndpoint.getPort()).invokeSync(invokeData);
                if (invokeResult.equals(InvokeResult.FAIL)) {
                   SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Invoke fail", "Tip"));
                }
            }
        });
    }

    @Override
    public void controllerChooseEvent(RequestMappingWrapper requestMappingModel) {
        this.requestMappingWrapper = requestMappingModel;
        if (requestMappingModel == null) return;
        switchPage(Panel.CONTROLLER);
//        this.httpRequestParamPanel.loadControllerInfo(requestMappingModel);
    }

    public void controllerChooseEvent(Controller controller) {
        this.currentSelectController = controller;
        if (controller == null) return;
        switchPage(Panel.CONTROLLER);
        this.httpRequestParamPanel.loadControllerInfo(controller);
    }
    public RequestMappingWrapper getRequestMappingWrapper() {
        return requestMappingWrapper;
    }

    public void scheduledChooseEvent(SpringScheduledSpringInvokeEndpoint scheduledEndpoint, int port) {
        this.selectSpringBootScheduledEndpoint = new SpringScheduledSpringInvokeEndpointWrapper(scheduledEndpoint, port);
        if (scheduledEndpoint == null) return;
        bottomScheduledUI.setText(scheduledEndpoint.getClassName() + "." + scheduledEndpoint.getMethodName());
        switchPage(Panel.SCHEDULED);
    }

    private void switchPage(Panel panel) {
        cardLayout.show(this, panel == Panel.CONTROLLER ?
                MainBottomHTTPInvokeRequestParamManagerPanel.class.getName()
                : BottomScheduledUI.class.getName());
    }

    @Override
    public void onHttpResponseEvent(String requestId, InvokeResponseModel invokeResponseModel) {
        requestManager.cancelHttpRequest(requestId);
        JavaCodeEngine javaCodeEngine = new JavaCodeEngine();
        RequestCache requestCache = RequestParamCacheManager.getCache(requestId);
        if (requestCache != null) {
            javaCodeEngine.execResponse(new com.hxl.plugin.springboot.invoke.script.Response(invokeResponseModel),
                    requestCache.getResponseScript(),
                    userProjectManager.getScriptSimpleLog());
        }
    }

    public void clearRequestParam() {
        this.httpRequestParamPanel.clearAllRequestParam();
    }

    public MainBottomHTTPInvokeRequestParamManagerPanel getHttpRequestParamPanel() {
        return httpRequestParamPanel;
    }

    private enum Panel {
        CONTROLLER, SCHEDULED
    }

}
