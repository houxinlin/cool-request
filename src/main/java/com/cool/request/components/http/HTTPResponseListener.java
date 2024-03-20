package com.cool.request.components.http;

import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.RequestContext;

public interface HTTPResponseListener {
    void onResponseEvent(String requestId, HTTPResponseBody httpResponseBody, RequestContext context);
}
