package com.cool.request.common.model;

import com.cool.request.component.http.net.HTTPResponseBody;

public class ReflexHTTPResponseBody extends HTTPResponseBody {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
