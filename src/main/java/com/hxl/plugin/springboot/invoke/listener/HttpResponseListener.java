package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;

public interface HttpResponseListener extends CommunicationListener {
    void onHttpResponseEvent(String requestId, InvokeResponseModel invokeResponseModel);
}
