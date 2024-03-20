package com.cool.request.common.bean;

import com.cool.request.components.http.ReflexRequestBody;

public class RefreshInvokeRequestBody extends ReflexRequestBody {
    public String getType() {
        return "refresh";
    }

}
