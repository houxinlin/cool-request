package com.cool.request.components.http;

import com.cool.request.components.http.net.HTTPHeader;

public interface HTTPResponseBodyConverter {
    public byte[] bodyConverter(byte[] httpResponseBody, HTTPHeader header);

}
