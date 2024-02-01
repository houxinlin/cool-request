package com.cool.request.component.http.invoke;

import com.cool.request.component.http.net.request.ReflexHttpRequestParamAdapter;
import com.cool.request.utils.ObjectMappingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ReflexControllerRequest extends BasicRemoteComponentRequest<ReflexHttpRequestParamAdapter> {
    public ReflexControllerRequest(int port) {
        super(port);
    }
    @Override
    public String createMessage(ReflexHttpRequestParamAdapter reflexHttpRequestParam) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(reflexHttpRequestParam);
        } catch (JsonProcessingException ignored) {
        }
        return "";
    }

}
