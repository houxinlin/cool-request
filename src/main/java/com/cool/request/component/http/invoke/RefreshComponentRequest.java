package com.cool.request.component.http.invoke;

import com.cool.request.common.bean.RefreshInvokeRequestBody;
import com.cool.request.utils.GsonUtils;

public class RefreshComponentRequest extends BasicRemoteComponentRequest<RefreshInvokeRequestBody> {
    public RefreshComponentRequest(int port) {
        super(port);
    }

    @Override
    public String createMessage(RefreshInvokeRequestBody refreshInvokeRequestBody) {
        return GsonUtils.toJsonString(refreshInvokeRequestBody);

    }
}
