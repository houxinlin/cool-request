package com.cool.request.view.main;

import com.cool.request.common.bean.components.Component;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.scheduled.BasicScheduled;
import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.component.CoolRequestPluginDisposable;
import com.cool.request.component.http.HTTPResponseListener;
import com.cool.request.component.http.net.HTTPHeader;
import com.cool.request.component.http.net.HTTPResponseBody;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.View;
import com.cool.request.view.page.HTTPResponseHeaderView;
import com.cool.request.view.page.HTTPResponseView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPResponseView extends JPanel implements View,
        Disposable, HTTPResponseListener {
    public static final String VIEW_ID = "@MainBottomHTTPResponseView";
    private final Project project;
    private HTTPResponseView httpResponseView;
    private HTTPResponseHeaderView httpResponseHeaderView;
    private TabInfo headerView;
    private TabInfo responseTabInfo;
    private Controller controller;
    private HTTPResponseBody httpResponseBody;

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

        //controller在选中的时候预览上次的响应结果
        connect.subscribe(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT, (CoolRequestIdeaTopic.ComponentChooseEventListener) controller -> {
            if (controller instanceof Controller || controller == null) {
                MainBottomHTTPResponseView.this.controller = ((Controller) controller);
                if (controller == null) return;
                CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
                HTTPResponseBody responseCache = service.getResponseCache(controller.getId());
                if (responseCache != null) {
                    onHttpResponseEvent(responseCache);
                }
            }

        });

        MessageBusConnection messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        messageBusConnection.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE,
                (CoolRequestIdeaTopic.BaseListener) this::loadText);
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), messageBusConnection);

    }

    //监听HTTP响应事件
    @Override
    public void onResponseEvent(String requestId, HTTPResponseBody httpResponseBody) {
        if (controller == null) return;
        //防止数据错位
        if (StringUtils.isEqualsIgnoreCase(this.controller.getId(), requestId)) {
            onHttpResponseEvent(httpResponseBody);
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
        JBTabsImpl tabs = new JBTabsImpl(project);
        httpResponseHeaderView = new HTTPResponseHeaderView(project);
        headerView = new TabInfo(httpResponseHeaderView);
        headerView.setText("Header");
        tabs.addTab(headerView);

        httpResponseView = new HTTPResponseView(project);
        responseTabInfo = new TabInfo(httpResponseView);
        responseTabInfo.setText("Response");
        tabs.addTab(responseTabInfo);

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA, (CoolRequestIdeaTopic.DeleteAllDataEventListener) () -> {
            httpResponseHeaderView.setText("");
            httpResponseView.reset();
        });
    }


    private void onHttpResponseEvent(HTTPResponseBody httpResponseBody) {
        this.httpResponseBody = httpResponseBody;
        SwingUtilities.invokeLater(() -> {
            byte[] response = Base64Utils.decode(httpResponseBody.getBase64BodyData());
            HTTPHeader httpHeader = new HTTPHeader(httpResponseBody.getHeader());
            httpResponseHeaderView.setText(httpHeader.headerToString());
            String contentType = "text/plain"; //默认的contentType
            httpResponseView.setResponseData(httpHeader.getContentType(contentType), response);
        });
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public String getViewId() {
        return VIEW_ID;
    }
}
