package com.cool.request.common.listener;

import com.cool.request.component.http.net.HTTPResponseBody;

public interface HttpResponseListener extends CommunicationListener {
    void onHttpResponseEvent(String requestId, HTTPResponseBody httpResponseBody);
}
