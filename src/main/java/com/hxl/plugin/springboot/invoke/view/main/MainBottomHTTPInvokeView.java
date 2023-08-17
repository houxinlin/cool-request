package com.hxl.plugin.springboot.invoke.view.main;

import javax.swing.*;

import com.hxl.plugin.springboot.invoke.bean.*;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.invoke.ScheduledInvoke;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.hxl.plugin.springboot.invoke.net.*;
import com.hxl.plugin.springboot.invoke.net.HttpRequest;
import com.hxl.plugin.springboot.invoke.net.BaseRequest;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.view.BottomScheduledUI;
import com.hxl.plugin.springboot.invoke.view.PluginWindowView;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MainBottomHTTPInvokeView extends JPanel  implements
        RequestMappingSelectedListener, BottomScheduledUI.InvokeClick {
    private final Project project;
    private final PluginWindowView pluginWindowView;
    private final HTTPRequestInfoPanel httpRequestInfoPanel;
    private final BottomScheduledUI bottomScheduledUI;
    private SpringMvcRequestMappingEndpointPlus selectSpringMvcRequestMappingEndpoint;
    private SpringBootScheduledEndpoint selectSpringBootScheduledEndpoint;
    private final ConcurrentHashMap<String, String> lastResponseCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> lastHeaderCache = new ConcurrentHashMap<>();
    private final CardLayout cardLayout = new CardLayout();
    private final int CONTROLLER_UI = 1;
    private final int SCHEDULED_UI = 0;

    public MainBottomHTTPInvokeView(@NotNull Project project, PluginWindowView pluginWindowView) {
        this.pluginWindowView = pluginWindowView;
        this.project = project;
        this.httpRequestInfoPanel = new HTTPRequestInfoPanel(project, this::sendRequest);
        this.bottomScheduledUI = new BottomScheduledUI(this);
        this.setLayout(cardLayout);
        this.add(bottomScheduledUI, BottomScheduledUI.class.getName());
        this.add(httpRequestInfoPanel, HTTPRequestInfoPanel.class.getName());
        switchPage(CONTROLLER_UI);

    }

    @Override
    public void onScheduledInvokeClick() {
        ScheduledInvoke.InvokeData invokeData = new ScheduledInvoke.InvokeData(selectSpringBootScheduledEndpoint.getId());
        int port = pluginWindowView.findPort(this.selectSpringBootScheduledEndpoint);
        new ScheduledInvoke(port).invoke(invokeData);
    }

    @Override
    public void requestMappingSelectedEvent(SpringMvcRequestMappingEndpointPlus springMvcRequestMappingEndpoint) {
        this.selectSpringMvcRequestMappingEndpoint = springMvcRequestMappingEndpoint;
        httpRequestInfoPanel.setSelectData(springMvcRequestMappingEndpoint);
        //设置最后一次的响应结果
        httpRequestInfoPanel.getHttpResponseEditor().setText(lastResponseCache.getOrDefault(springMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId(), ""));
       // HTTPRequestInfoPanel.getHttpHeaderEditor().setText(lastHeaderCache.getOrDefault(springMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId(), ""));
        switchPage(CONTROLLER_UI);
    }

    @Override
    public void scheduledSelectedEvent(SpringBootScheduledEndpoint scheduledEndpoint) {
        this.selectSpringBootScheduledEndpoint = scheduledEndpoint;
        bottomScheduledUI.setText(scheduledEndpoint.getClassName() + "." + scheduledEndpoint.getMethodName());
        switchPage(SCHEDULED_UI);
    }

    private void switchPage(int target) {
        cardLayout.show(this, target == 0 ? BottomScheduledUI.class.getName() : HTTPRequestInfoPanel.class.getName());
    }



    public void sendRequest() {

        //使用用户输入的url和method
        String url = httpRequestInfoPanel.getRequestUrl();
        BeanInvokeSetting beanInvokeSetting = httpRequestInfoPanel.getBeanInvokeSetting();
        // Map<String, Object> requestHeader = HTTPRequestInfoPanel.getRequestHeader();
        int port = pluginWindowView.findPort(this.selectSpringMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint());

        String httpMethod = httpRequestInfoPanel.getHttpMethod().toString();

        ControllerInvoke.ControllerRequestData controllerRequestData = new ControllerInvoke.ControllerRequestData(httpMethod, url,this.selectSpringMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId(),
                beanInvokeSetting.isUseProxy(), beanInvokeSetting.isUseInterceptor(), false);

        httpRequestInfoPanel.applyRequestInfo(controllerRequestData);
        //选择调用方式
        HttpRequest.SimpleCallback simpleCallback = new HttpRequest.SimpleCallback() {
            @Override
            public void onResponse(String requestId, int code, Map<String, List<String>> headers, byte[] response) {
//                MainBottomHTTPInvokeView.this.onResponse(requestId, headers, response);
            }

            @Override
            public void onError(IOException e) {

            }
        };
        //保存缓存
        RequestParamCacheManager.setCache(this.selectSpringMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId(), RequestCache.RequestCacheBuilder.aRequestCache()
                .withRequestBody("")
                .withUrl(url)
                .withPort(port)
                .withContentPath(this.selectSpringMvcRequestMappingEndpoint.getContextPath())
                .withUseProxy(beanInvokeSetting.isUseProxy())
                .withUseInterceptor(beanInvokeSetting.isUseInterceptor())
                .withInvokeModelIndex(httpRequestInfoPanel.getInvokeModelIndex())
                .build());

        if (!checkUrl(url)) {
            NotifyUtils.notification(project, "Invalid URL");
            return;
        }
        BaseRequest baseRequest = httpRequestInfoPanel.getInvokeModelIndex() == 1 ? new ReflexRequest(controllerRequestData, port) : new HttpRequest(controllerRequestData, simpleCallback);
        baseRequest.invoke();

    }

    private boolean checkUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException ignored) {
        }
        return false;
    }

}
