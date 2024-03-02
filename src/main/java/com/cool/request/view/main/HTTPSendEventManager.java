package com.cool.request.view.main;

import com.cool.request.common.bean.components.controller.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP 发送/结束事件响应器
 */
public class HTTPSendEventManager {
    private final List<HTTPSendListener> httpSendListeners = new ArrayList<>();

    public void register(HTTPSendListener httpSendListener) {
        httpSendListeners.add(httpSendListener);
    }

    public void sendEnd(Controller controller) {
        for (HTTPSendListener httpSendListener : httpSendListeners) {
            httpSendListener.endSend(controller);
        }
    }

    public void sendBegin(Controller controller) {
        for (HTTPSendListener httpSendListener : httpSendListeners) {
            httpSendListener.beginSend(controller);
        }
    }
}
