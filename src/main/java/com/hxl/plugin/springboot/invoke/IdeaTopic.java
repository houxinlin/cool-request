package com.hxl.plugin.springboot.invoke;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.intellij.util.messages.Topic;

public class IdeaTopic {
    public static final Topic<HttpResponseEventListener> HTTP_RESPONSE = new Topic("HTTP_RESPONSE", HttpResponseEventListener.class);
    public static final Topic<HttpResponseEventListener> HTTP_REQUEST_CANCEL = new Topic("HTTP_REQUEST_CANCEL", HttpRequestCancelEventListener.class);
    public interface HttpResponseEventListener {
        void onResponseEvent(String requestId, InvokeResponseModel invokeResponseModel);
    }
    @FunctionalInterface
    public interface HttpRequestCancelEventListener {
        void onCancelEvent(String requestId);
    }
}
