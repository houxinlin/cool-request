package com.cool.request.view.main;

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


}
