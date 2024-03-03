package com.cool.request.component.http;

import com.cool.request.component.http.net.HTTPResponseBody;

public interface HTTPResponseListener {
    void onResponseEvent(String requestId, HTTPResponseBody httpResponseBody);
}
