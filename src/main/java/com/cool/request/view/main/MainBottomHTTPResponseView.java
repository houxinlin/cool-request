/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MainBottomHTTPResponseView.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.main;

import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.net.HTTPHeader;
import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.RequestContext;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.View;
import com.cool.request.view.page.HTTPResponseHeaderView;
import com.cool.request.view.page.HTTPResponseView;
import com.cool.request.view.page.TracePreviewView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import java.awt.*;

@HTTPEventOrder(HTTPEventOrder.MAX)
public class MainBottomHTTPResponseView extends JPanel implements
        View,
        HTTPEventListener {
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

    public MainBottomHTTPResponseView(final Project project) {
        this.project = project;
        initUI();
        settingChange();
        MessageBusConnection messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        messageBusConnection.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, this::settingChange);

        Disposer.register(CoolRequestPluginDisposable.getInstance(project), messageBusConnection);

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

        if (SettingPersistentState.getInstance().getState().enabledTrace) {
            jbTabs.addTab(traceTabInfo);
        }

        jbTabs.select(responseTabInfo, true);
        this.setLayout(new BorderLayout());
        this.add(httpResponseStatus.getRoot(), BorderLayout.NORTH);
        this.add(jbTabs, BorderLayout.CENTER);
    }

    public boolean isTabAdded(TabInfo tabInfo) {
        for (TabInfo existingTab : jbTabs.getTabs()) {
            if (existingTab.equals(tabInfo)) {
                return true;
            }
        }
        return false;
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
    public void endSend(RequestContext requestContext, HTTPResponseBody httpResponseBody, ProgressIndicator progressIndicator) {
        progressIndicator.setText("Parse response body");
        if (this.controller == null) return;
        if (StringUtils.isEqualsIgnoreCase(requestContext.getId(), this.controller.getId())) {
            onHttpResponseEvent(httpResponseBody, requestContext);
        }
    }

    public HTTPResponseBody getHttpResponseBody() {
        return httpResponseBody;
    }

    private void settingChange() {
        headerView.setText(ResourceBundleUtils.getString("header"));
        responseTabInfo.setText(ResourceBundleUtils.getString("response"));

        if (SettingPersistentState.getInstance().getState().enabledTrace && !isTabAdded(traceTabInfo)) {
            jbTabs.addTab(traceTabInfo);
        }

        if (!SettingPersistentState.getInstance().getState().enabledTrace && isTabAdded(traceTabInfo)) {
            jbTabs.removeTab(traceTabInfo);
        }
    }


    private void onHttpResponseEvent(HTTPResponseBody httpResponseBody, RequestContext requestContext) {
        this.httpResponseBody = httpResponseBody;
        httpResponseStatus.getRoot().setVisible(requestContext != null);
        if (httpResponseBody == null) return;
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
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
        });
    }

    public TracePreviewView getTracePreviewView() {
        return tracePreviewView;
    }

    @Override
    public String getViewId() {
        return VIEW_ID;
    }
}
