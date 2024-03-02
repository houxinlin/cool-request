package com.cool.request.component.http;

import com.cool.request.component.http.net.HTTPHeader;
import com.cool.request.component.http.net.MediaTypes;

public class OverflowHttpResponseBodyConverter implements HTTPResponseBodyConverter {
    @Override
    public byte[] bodyConverter(byte[] httpResponseBody, HTTPHeader header) {
        if (httpResponseBody.length > 2 * 1024 * 1024) {
            header.setHeader(HTTPHeader.CONTENT_TYPE, MediaTypes.TEXT);
            return "数据过大,无法显示".getBytes();
        } else {
            return httpResponseBody;
        }
    }
}
