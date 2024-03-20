package com.cool.request.common.model;

import com.cool.request.components.http.net.HTTPResponseBody;

public class ReflexHTTPResponseBody extends HTTPResponseBody {
    private String type;
    private Long attachData;

    public Long getAttachData() {
        return attachData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
