package com.cool.request.component.http.invoke;

import com.cool.request.common.bean.RefreshInvokeRequestBody;
import com.cool.request.utils.ObjectMappingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RefreshComponentRequest extends BasicRemoteComponentRequest<RefreshInvokeRequestBody> {
    public RefreshComponentRequest(int port) {
        super(port);
    }
    @Override
    public String createMessage(RefreshInvokeRequestBody refreshInvokeRequestBody) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(refreshInvokeRequestBody);
        } catch (JsonProcessingException ignored) {

        }
        return "";
    }
}
