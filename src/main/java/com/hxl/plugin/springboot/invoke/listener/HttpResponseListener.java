package com.hxl.plugin.springboot.invoke.listener;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;

import java.util.List;
import java.util.Map;

public interface HttpResponseListener extends CommunicationListener {
    public void onResponse(String requestId, List<InvokeResponseModel.Header>   headers, String response);
}
