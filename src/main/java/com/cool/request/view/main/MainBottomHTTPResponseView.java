package com.cool.request.view.main;

import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.net.HTTPHeader;
import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.RequestContext;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.View;
import com.cool.request.view.page.HTTPResponseHeaderView;
import com.cool.request.view.page.HTTPResponseView;
import com.cool.request.view.page.TracePreviewView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPResponseView extends JPanel implements View,
        Disposable, HTTPEventListener {
    public static final String VIEW_ID = "@MainBottomHTTPResponseView";
    private final Project project;
    private HTTPResponseView httpResponseView;
    private HTTPResponseHeaderView httpResponseHeaderView;
    private TracePreviewView tracePreviewView;
    private TabInfo headerView;
    private TabInfo responseTabInfo;
    private TabInfo traceTabInfo;
    private Controller controller;
    private HTTPResponseBody httpResponseBody;
    private JBTabsImpl jbTabs;
    private HTTPResponseStatusPanel httpResponseStatus = new HTTPResponseStatusPanel();

    @Override
    public void dispose() {
        httpResponseHeaderView.dispose();
//        httpResponseHeaderView.dispose();
//        httpResponseView.dispose();
    }

    public MainBottomHTTPResponseView(final Project project) {
        this.project = project;
        initUI();
        MessageBusConnection connect = project.getMessageBus().connect();
        loadText();
        connect.subscribe(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT, (CoolRequestIdeaTopic.ComponentChooseEventListener) component -> {
            if (component instanceof BasicScheduled) {
                httpResponseHeaderView.setText("");
                httpResponseView.reset();
            }
        });

        MessageBusConnection messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        messageBusConnection.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, this::loadText);
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), messageBusConnection);

    }

    public void controllerChoose(Controller newController) {
        this.controller = newController;
        tracePreviewView.setController(newController);
        if (controller == null) return;
        CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
        HTTPResponseBody responseCache = service.getResponseCache(controller.getId());
        if (responseCache != null) {
            onHttpResponseEvent(responseCache, null);
        }
    }
    //监听HTTP响应事件

    @Override
    public void beginSend(RequestContext requestContext, ProgressIndicator progressIndicator) {

    }

    @Override
    public void endSend(RequestContext requestContext, HTTPResponseBody httpResponseBody) {
        if (controller == null) return;
        //防止数据错位
        if (StringUtils.isEqualsIgnoreCase(this.controller.getId(), requestContext.getId())) {
            onHttpResponseEvent(httpResponseBody, requestContext);
        }
    }

    public HTTPResponseBody getInvokeResponseModel() {
        return httpResponseBody;
    }

    private void loadText() {
        headerView.setText(ResourceBundleUtils.getString("header"));
        responseTabInfo.setText(ResourceBundleUtils.getString("response"));
    }

    private void initUI() {
        jbTabs = new JBTabsImpl(project);
        httpResponseHeaderView = new HTTPResponseHeaderView(project);
        headerView = new TabInfo(httpResponseHeaderView);
        headerView.setText("Header");
        jbTabs.addTab(headerView);

        httpResponseView = new HTTPResponseView(project);
        responseTabInfo = new TabInfo(httpResponseView);
        responseTabInfo.setText("Response");
        jbTabs.addTab(responseTabInfo);


        tracePreviewView = new TracePreviewView(project);
        traceTabInfo = new TabInfo(tracePreviewView);
        traceTabInfo.setText("Trace");
        jbTabs.addTab(traceTabInfo);

        jbTabs.select(responseTabInfo, true);
        this.setLayout(new BorderLayout());
        this.add(httpResponseStatus.getRoot(), BorderLayout.NORTH);
        this.add(jbTabs, BorderLayout.CENTER);
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA, () -> {
            httpResponseHeaderView.setText("");
            httpResponseView.reset();
        });
    }


    private void onHttpResponseEvent(HTTPResponseBody httpResponseBody, RequestContext requestContext) {
        this.httpResponseBody = httpResponseBody;
        httpResponseStatus.getRoot().setVisible(requestContext != null);
        if (httpResponseBody == null) return;
        new Thread(() -> {
            byte[] response = Base64Utils.decode(httpResponseBody.getBase64BodyData());
            HTTPHeader httpHeader = new HTTPHeader(httpResponseBody.getHeader());
            SwingUtilities.invokeLater(() -> {
                if (requestContext != null) {
                    httpResponseStatus.parse(httpResponseBody, requestContext);
                }
                httpResponseHeaderView.setText(httpHeader.headerToString());
                String contentType = "text/plain"; //默认的contentType
                httpResponseView.setResponseData(httpHeader.getContentType(contentType), response);

            });
        }).start();

    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public TracePreviewView getTracePreviewView() {
        return tracePreviewView;
    }

    @Override
    public String getViewId() {
        return VIEW_ID;
    }
}
