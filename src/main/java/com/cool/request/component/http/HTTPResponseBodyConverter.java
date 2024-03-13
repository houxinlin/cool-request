package com.cool.request.component.http;

import com.cool.request.component.http.net.HTTPHeader;
import com.cool.request.component.http.net.HTTPResponseBody;

public interface HTTPResponseBodyConverter {
    public byte[] bodyConverter(byte[] httpResponseBody, HTTPHeader header);

}
