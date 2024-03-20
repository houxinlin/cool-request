package com.cool.request.view.main;

import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.RequestContext;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP 发送/结束事件响应器
 */
public class HTTPEventManager {
    private final List<HTTPEventListener> httpEventListeners = new ArrayList<>();

    public void register(HTTPEventListener httpEventListener) {
        httpEventListeners.add(httpEventListener);
    }

    public List<HTTPEventListener> getHttpEventListeners() {
        return httpEventListeners;
    }

    public void sendEnd(RequestContext requestContext, HTTPResponseBody httpResponseBody) {
        for (HTTPEventListener httpEventListener : httpEventListeners) {
            httpEventListener.endSend(requestContext, httpResponseBody);
        }
    }

    public void sendBegin(RequestContext requestContext) {
        for (HTTPEventListener httpEventListener : httpEventListeners) {
            httpEventListener.beginSend(requestContext);
        }
    }
}
