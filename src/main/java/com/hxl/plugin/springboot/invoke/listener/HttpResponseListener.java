package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;

public interface HttpResponseListener extends CommunicationListener {
    public void onResponse(String requestId, InvokeResponseModel invokeResponseModel);
}
