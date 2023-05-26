package com.hxl.plugin.springboot.invoke.view;

import javax.swing.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxl.plugin.springboot.invoke.bean.*;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.listener.HttpResponseListener;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.hxl.plugin.springboot.invoke.net.*;
import com.hxl.plugin.springboot.invoke.net.HttpRequest;
import com.hxl.plugin.springboot.invoke.net.BaseRequest;
import com.hxl.plugin.springboot.invoke.utils.HeaderUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BottomHttpDetailView extends JPanel implements HttpResponseListener, RequestMappingSelectedListener {
    private final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("com.hxl.plugin.scheduled-invoke", NotificationDisplayType.BALLOON, true);
    private final Project project;
    private final PluginWindowView pluginWindowView;
    private final BottomHttpUI bottomHttpUI;
    private SpringMvcRequestMappingEndpointPlus selectSpringMvcRequestMappingEndpoint;
    private final ConcurrentHashMap<String, String> lastResponseCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> lastHeaderCache = new ConcurrentHashMap<>();

    public BottomHttpDetailView(@NotNull Project project, PluginWindowView pluginWindowView) {
        this.pluginWindowView = pluginWindowView;
        this.project = project;
        this.bottomHttpUI = new BottomHttpUI(this, project);
    }

    @Override
    public void selectRequestMappingSelectedEvent(SpringMvcRequestMappingEndpointPlus springMvcRequestMappingEndpoint) {
        this.selectSpringMvcRequestMappingEndpoint = springMvcRequestMappingEndpoint;
        bottomHttpUI.setSelectData(springMvcRequestMappingEndpoint);
        //设置最后一次的响应结果
        bottomHttpUI.getHttpResponseEditor().setText(lastResponseCache.getOrDefault(springMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId(), ""));
        bottomHttpUI.getHttpHeaderEditor().setText(lastHeaderCache.getOrDefault(springMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId(),""));
    }

    private void notification(String msg) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
        notification.notify(this.project);
    }

    public void sendRequest() {
        try {
            //使用用户输入的url和method
            String url = bottomHttpUI.getRequestUrl();
            BeanInvokeSetting beanInvokeSetting = bottomHttpUI.getBeanInvokeSetting();
            Map<String, Object> requestHeader = bottomHttpUI.getRequestHeader();
            int port = pluginWindowView.findPort(this.selectSpringMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint());

            String httpMethod = bottomHttpUI.getHttpMethod().toString();

            ControllerInvoke.InvokeData invokeData = new ControllerInvoke.InvokeData(httpMethod, url, bottomHttpUI.getRequestBodyFileType(),
                    bottomHttpUI.getRequestBody(), this.selectSpringMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId(),
                    beanInvokeSetting.isUseProxy(), beanInvokeSetting.isUseInterceptor(), false, requestHeader);

            //选择调用方式
            HttpRequest.SimpleCallback simpleCallback = new HttpRequest.SimpleCallback() {
                @Override
                public void onResponse(String requestId, int code, Map<String, List<String>> headers, byte[] response) {
                    BottomHttpDetailView.this.onResponse(requestId, headers, response);
                }

                @Override
                public void onError(IOException e) {

                }
            };

            //保存缓存
            RequestParamCacheManager.setCache(this.selectSpringMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId(), RequestCache.RequestCacheBuilder.aRequestCache()
                    .withRequestBody(bottomHttpUI.getRequestBody())
                    .withUrl(url)
                    .withPort(port)
                    .withContentPath(this.selectSpringMvcRequestMappingEndpoint.getContextPath())
                    .withRequestHeader(requestHeader)
                    .withUseProxy(beanInvokeSetting.isUseProxy())
                    .withUseInterceptor(beanInvokeSetting.isUseInterceptor())
                    .withInvokeModelIndex(bottomHttpUI.getInvokeModelIndex())
                    .build());

            if (!checkUrl(url)) {
                notification("Invalid URL");
                return;
            }
            BaseRequest baseRequest = bottomHttpUI.getInvokeModelIndex() == 1 ? new ReflexRequest(invokeData, port) : new HttpRequest(invokeData, simpleCallback);
            baseRequest.invoke();
        } catch (JsonProcessingException e) {
            notification("Request header format error");
        }

    }

    private boolean checkUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException ignored) {
        }
        return false;
    }

    @Override
    public void onResponse(String requestId, Map<String, List<String>> headers, byte[] response) {
        SwingUtilities.invokeLater(() -> {
            bottomHttpUI.setHttpResponse(requestId, headers, response);
            lastResponseCache.put(requestId, new String(response));
            lastHeaderCache.put(requestId, HeaderUtils.headerToString(headers));
        });
    }
}
