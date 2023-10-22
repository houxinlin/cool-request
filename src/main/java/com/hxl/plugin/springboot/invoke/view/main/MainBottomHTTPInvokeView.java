package com.hxl.plugin.springboot.invoke.view.main;

import javax.swing.*;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.*;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.model.ErrorInvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;
import com.hxl.plugin.springboot.invoke.invoke.ScheduledInvoke;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.hxl.plugin.springboot.invoke.net.*;
import com.hxl.plugin.springboot.invoke.net.HttpRequest;
import com.hxl.plugin.springboot.invoke.net.BaseRequest;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.ProjectUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.view.BottomScheduledUI;
import com.hxl.plugin.springboot.invoke.view.IRequestParamManager;
import com.hxl.plugin.springboot.invoke.view.PluginWindowToolBarView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import kotlin.Pair;
import okhttp3.Headers;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public class MainBottomHTTPInvokeView extends JPanel implements
        RequestMappingSelectedListener, BottomScheduledUI.InvokeClick, HttpResponseListener {
    private final Project project;
    private final PluginWindowToolBarView pluginWindowView;
    private final HTTPRequestParamManagerPanel httpRequestParamPanel;
    private final BottomScheduledUI bottomScheduledUI;
    private RequestMappingModel requestMappingModel;
    private SpringScheduledSpringInvokeEndpoint selectSpringBootScheduledEndpoint;
    private final Map<String, Boolean> buttonStateMap = new HashMap<>();
    private final CardLayout cardLayout = new CardLayout();
    private final Map<String, Thread> waitResponseThread = new ConcurrentHashMap<>();

    public MainBottomHTTPInvokeView(@NotNull Project project, PluginWindowToolBarView pluginWindowView) {
        this.pluginWindowView = pluginWindowView;
        this.project = project;
        this.httpRequestParamPanel = new HTTPRequestParamManagerPanel(project, this);
        this.bottomScheduledUI = new BottomScheduledUI(this);
        this.setLayout(cardLayout);
        this.add(bottomScheduledUI, BottomScheduledUI.class.getName());
        this.add(httpRequestParamPanel, HTTPRequestParamManagerPanel.class.getName());
        switchPage(Panel.CONTROLLER);
    }

    public String getSelectRequestMappingId() {
        if (this.requestMappingModel != null) return this.requestMappingModel.getController().getId();
        return "";
    }

    public boolean canEnabledSendButton(String id) {
        return buttonStateMap.getOrDefault(id, true);
    }

    @Override
    public void onScheduledInvokeClick() {
        ScheduledInvoke.InvokeData invokeData = new ScheduledInvoke.InvokeData(selectSpringBootScheduledEndpoint.getId());
        int port = pluginWindowView.findPort(this.selectSpringBootScheduledEndpoint);
        new ScheduledInvoke(port).invoke(invokeData);
    }

    @Override
    public void controllerChooseEvent(RequestMappingModel select) {
        this.requestMappingModel = select;
        httpRequestParamPanel.setSelectData(select);
        switchPage(Panel.CONTROLLER);
    }


    @Override
    public void scheduledChooseEvent(SpringScheduledSpringInvokeEndpoint scheduledEndpoint) {
        this.selectSpringBootScheduledEndpoint = scheduledEndpoint;
        bottomScheduledUI.setText(scheduledEndpoint.getClassName() + "." + scheduledEndpoint.getMethodName());
        switchPage(Panel.SCHEDULED);
    }

    private void switchPage(Panel panel) {
        if (panel == Panel.CONTROLLER) cardLayout.show(this, HTTPRequestParamManagerPanel.class.getName());
        if (panel == Panel.SCHEDULED) cardLayout.show(this, BottomScheduledUI.class.getName());
    }


    public void sendRequest() {
        //使用用户输入的url和method
        String url = httpRequestParamPanel.getRequestParamManager().getUrl();
        IRequestParamManager requestParamManager = httpRequestParamPanel.getRequestParamManager();
        BeanInvokeSetting beanInvokeSetting = httpRequestParamPanel.getBeanInvokeSetting();
        // Map<String, Object> requestHeader = HTTPRequestInfoPanel.getRequestHeader();
        int port = pluginWindowView.findPort(this.requestMappingModel.getController());
        String httpMethod = httpRequestParamPanel.getHttpMethod().toString();
        //创建请求参数对象
        ControllerInvoke.ControllerRequestData controllerRequestData =
                new ControllerInvoke.ControllerRequestData(httpMethod, url, this.requestMappingModel.getController().getId(),
                        beanInvokeSetting.isUseProxy(), beanInvokeSetting.isUseInterceptor(), false);
        //设置请求参数
        httpRequestParamPanel.applyRequestParams(controllerRequestData);
        //选择调用方式

        //保存缓存
        RequestParamCacheManager.setCache(this.requestMappingModel.getController().getId(), RequestCache.RequestCacheBuilder.aRequestCache()
                .withHeaders(requestParamManager.getHttpHeader())
                .withUrlParams(requestParamManager.getUrlParam())
                .withRequestBodyType(requestParamManager.getRequestBodyType())
                .withFormDataInfos(requestParamManager.getFormData())
                .withUrlencodedBody(requestParamManager.getUrlencodedBody())
                .withRequestBody(requestParamManager.getRequestBody())
                .withUrl(url)
                .withPort(port)
                .withContentPath(this.requestMappingModel.getContextPath())
                .withUseProxy(beanInvokeSetting.isUseProxy())
                .withUseInterceptor(beanInvokeSetting.isUseInterceptor())
                .withInvokeModelIndex(httpRequestParamPanel.getInvokeModelIndex())
                .build());

        if (!checkUrl(url)) {
            NotifyUtils.notification(project, "Invalid URL");
            return;
        }

        HttpRequest.SimpleCallback simpleCallback = new HttpRequest.SimpleCallback() {
            @Override
            public void onResponse(String requestId, int code, Response response) {
                Headers okHttpHeaders = response.headers();
                List<InvokeResponseModel.Header> headers = new ArrayList<>();
                int headerCount = okHttpHeaders.size();
                for (int i = 0; i < headerCount; i++) {
                    String headerName = okHttpHeaders.name(i);
                    String headerValue = okHttpHeaders.value(i);
                    headers.add(new InvokeResponseModel.Header(headerName, headerValue));
                }
                InvokeResponseModel invokeResponseModel = new InvokeResponseModel();
                invokeResponseModel.setData("");
                if (response.body() != null) {
                    try {
                        invokeResponseModel.setData(response.body().string());
                    } catch (IOException e) {
                    }
                }
                invokeResponseModel.setHeader(headers);
                ApplicationManager.getApplication().getMessageBus().syncPublisher(IdeaTopic.HTTP_RESPONSE).onResponseEvent(requestId, invokeResponseModel);
            }

            @Override
            public void onError(String requestId, IOException e) {
                ApplicationManager.getApplication()
                        .getMessageBus()
                        .syncPublisher(IdeaTopic.HTTP_RESPONSE)
                        .onResponseEvent(requestId, new ErrorInvokeResponseModel(e.getMessage()));
            }
        };
        BaseRequest baseRequest = httpRequestParamPanel.getInvokeModelIndex() == 1 ?
                new ReflexRequest(controllerRequestData, port) :
                new HttpRequest(controllerRequestData, simpleCallback);
        if (!runNewHttpRequestProgressTask(this.requestMappingModel, baseRequest)) {
            Messages.showErrorDialog("无法执行，等待上一个任务结束", "提示");
        }
    }

    @Override
    public void onResponse(String requestId, InvokeResponseModel invokeResponseModel) {
        cancelHttpRequest(requestId);
    }

    private void cancelHttpRequest(String requestId) {
        Thread thread = waitResponseThread.get(requestId);
        if (thread != null) {
            LockSupport.unpark(thread);
            waitResponseThread.remove(requestId);
        }
        buttonStateMap.remove(requestId);
        if (requestId.equalsIgnoreCase(this.requestMappingModel.getController().getId())) {
            httpRequestParamPanel.setSendButtonEnabled(true);
        }
    }

    public boolean runNewHttpRequestProgressTask(RequestMappingModel requestMappingModel, BaseRequest baseRequest) {
        String invokeId = requestMappingModel.getController().getId();
        if (waitResponseThread.containsKey(invokeId)) {
            return false;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(ProjectUtils.getCurrentProject(), "Send request") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                buttonStateMap.put(requestMappingModel.getController().getId(), false);
                Thread thread = Thread.currentThread();
                waitResponseThread.put(invokeId, thread);
                baseRequest.invoke();
                indicator.setText("Wait " + requestMappingModel.getController().getUrl() + " response");
                while (!indicator.isCanceled() && waitResponseThread.containsKey(invokeId)) {
                    LockSupport.parkNanos(thread, 500);
                }
                if (indicator.isCanceled())
                    MainBottomHTTPInvokeView.this.cancelHttpRequest(requestMappingModel.getController().getId());
            }
        });
        return true;
    }

    private boolean checkUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException ignored) {
        }
        return false;
    }

    private enum Panel {
        CONTROLLER, SCHEDULED
    }

}
