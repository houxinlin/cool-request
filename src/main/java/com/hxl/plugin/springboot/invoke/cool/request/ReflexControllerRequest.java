package com.hxl.plugin.springboot.invoke.cool.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxl.plugin.springboot.invoke.invoke.BasicRemoteComponentRequest;
import com.hxl.plugin.springboot.invoke.net.request.ReflexHttpRequestParam;
import com.hxl.plugin.springboot.invoke.net.request.ReflexHttpRequestParamAdapter;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;

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
