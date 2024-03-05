package com.cool.request.component.http;

import com.cool.request.component.http.net.HTTPResponseBody;
import com.cool.request.component.http.net.RequestContext;

public interface HTTPResponseListener {
    void onResponseEvent(String requestId, HTTPResponseBody httpResponseBody, RequestContext context);
}
