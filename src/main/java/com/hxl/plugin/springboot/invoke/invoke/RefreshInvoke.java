package com.hxl.plugin.springboot.invoke.invoke;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxl.plugin.springboot.invoke.bean.RefreshInvokeRequestBody;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;

public class RefreshInvoke extends BasicRemoteInvoke<RefreshInvokeRequestBody> {
    public RefreshInvoke(int port) {
        super(port);
    }
    @Override
    public String createMessage(RefreshInvokeRequestBody refreshInvokeRequestBody) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(refreshInvokeRequestBody);
        } catch (JsonProcessingException e) {

        }
        return "";
    }
}
