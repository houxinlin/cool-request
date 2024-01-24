package com.cool.request.listener;

import com.cool.request.model.InvokeResponseModel;

public interface HttpResponseListener extends CommunicationListener {
    void onHttpResponseEvent(String requestId, InvokeResponseModel invokeResponseModel);
}
