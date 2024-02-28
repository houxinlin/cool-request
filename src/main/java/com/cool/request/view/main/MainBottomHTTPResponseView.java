package com.cool.request.view.main;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.component.CoolRequestPluginDisposable;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.View;
import com.cool.request.view.page.HTTPResponseHeaderView;
import com.cool.request.view.page.HTTPResponseView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPResponseView extends JPanel implements View {
    public static final String VIEW_ID = "@MainBottomHTTPResponseView";
    private final Project project;
    private HTTPResponseView httpResponseView;
    private HTTPResponseHeaderView httpResponseHeaderView;
    private TabInfo headerView;
    private TabInfo responseTabInfo;
    private Controller controller;
    private InvokeResponseModel invokeResponseModel;

    public MainBottomHTTPResponseView(final Project project) {
        this.project = project;
        initUI();
        MessageBusConnection connect = project.getMessageBus().connect();
        loadText();
        connect.subscribe(CoolRequestIdeaTopic.SCHEDULED_CHOOSE_EVENT, (CoolRequestIdeaTopic.ScheduledChooseEventListener) (springScheduledSpringInvokeEndpoint) -> {
            httpResponseHeaderView.setText("");
            httpResponseView.reset();
        });

        //controller在选中的时候预览上次的响应结果
        connect.subscribe(CoolRequestIdeaTopic.CONTROLLER_CHOOSE_EVENT, (CoolRequestIdeaTopic.ControllerChooseEventListener) controller -> {
            MainBottomHTTPResponseView.this.controller = controller;
            if (controller == null) return;
            CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
            InvokeResponseModel responseCache = service.loadResponseCache(controller.getId());
            if (responseCache != null) {
                onHttpResponseEvent(responseCache);
            }
        });

        //监听HTTP响应事件
        connect.subscribe(CoolRequestIdeaTopic.HTTP_RESPONSE, (CoolRequestIdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            //防止数据错位
            if (controller == null) return;
            if (StringUtils.isEqualsIgnoreCase(this.controller.getId(), requestId)) {
                onHttpResponseEvent(invokeResponseModel);
            }
        });
        MessageBusConnection messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        messageBusConnection.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE,
                (CoolRequestIdeaTopic.BaseListener) this::loadText);
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), messageBusConnection);

    }

    public InvokeResponseModel getInvokeResponseModel() {
        return invokeResponseModel;
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


    private void onHttpResponseEvent(InvokeResponseModel invokeResponseModel) {
        this.invokeResponseModel = invokeResponseModel;
        SwingUtilities.invokeLater(() -> {
            byte[] response = Base64Utils.decode(invokeResponseModel.getBase64BodyData());
            httpResponseHeaderView.setText(invokeResponseModel.headerToString());
            String contentType = "text/plain"; //默认的contentType
            for (InvokeResponseModel.Header header : invokeResponseModel.getHeader()) {
                if ("content-type".equalsIgnoreCase(header.getKey()) && !StringUtils.isEmpty(header.getValue())) {
                    contentType = header.getValue();
                }
            }
            httpResponseView.setResponseData(contentType, response);
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
