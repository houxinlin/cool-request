package com.cool.request.component.http.net;

import com.cool.request.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class HTTPHeader {
    public static final String CONTENT_TYPE = "Content-Type";
    private List<Header> headers;

    public HTTPHeader(List<Header> headers) {
        this.headers = headers;
        if (this.headers == null) this.headers = new ArrayList<>();
    }

    public String headerToString() {
        StringBuilder headerStringBuffer = new StringBuilder();
        for (Header header : headers) {
            headerStringBuffer.append(header.getKey()).append(": ").append(header.getValue());
            headerStringBuffer.append("\n");
        }
        return headerStringBuffer.toString();
    }

    public String getContentType(String defaultValue) {
        String contentType = defaultValue;
        for (Header header : headers) {
            if ("content-type".equalsIgnoreCase(header.getKey()) && !StringUtils.isEmpty(header.getValue())) {
                contentType = header.getValue();
            }
        }
        return contentType;
    }

    public void setHeader(String key, String value) {
        if (headers == null) return;
        headers.removeIf(header -> StringUtils.isEqualsIgnoreCase(header.getKey(), key));
        headers.add(new Header(key, value));
    }
}
