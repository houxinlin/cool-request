package com.cool.request.common.listener;

import com.cool.request.common.model.InvokeResponseModel;

public interface HttpResponseListener extends CommunicationListener {
    void onHttpResponseEvent(String requestId, InvokeResponseModel invokeResponseModel);
}
