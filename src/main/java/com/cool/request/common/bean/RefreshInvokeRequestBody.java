package com.cool.request.common.bean;

import com.cool.request.component.http.invoke.ReflexRequestBody;

public class RefreshInvokeRequestBody extends ReflexRequestBody {
    public String getType() {
        return "refresh";
    }

}
