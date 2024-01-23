package com.hxl.plugin.springboot.invoke.cool.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxl.plugin.springboot.invoke.invoke.BasicRemoteComponentRequest;
import com.hxl.plugin.springboot.invoke.net.request.ReflexHttpRequestParam;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;

public class ReflexControllerRequest extends BasicRemoteComponentRequest<ReflexHttpRequestParam> {
    public ReflexControllerRequest(int port) {
        super(port);
    }
    @Override
    public String createMessage(ReflexHttpRequestParam reflexHttpRequestParam) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(reflexHttpRequestParam);
        } catch (JsonProcessingException ignored) {
        }
        return "";
    }

}
