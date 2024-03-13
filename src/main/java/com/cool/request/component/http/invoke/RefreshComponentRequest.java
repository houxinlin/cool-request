package com.cool.request.component.http.invoke;

import com.cool.request.common.bean.RefreshInvokeRequestBody;

public class RefreshComponentRequest extends BasicRemoteComponentRequest<RefreshInvokeRequestBody> {
    public RefreshComponentRequest(int port) {
        super(port);
    }
}
