package com.hxl.plugin.springboot.invoke.cool.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxl.plugin.springboot.invoke.invoke.BasicRemoteComponentRequest;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;

public class ReflexControllerRequest extends BasicRemoteComponentRequest<ControllerRequestData> {
    public ReflexControllerRequest(int port) {
        super(port);
    }
    @Override
    public String createMessage(ControllerRequestData controllerRequestData) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(controllerRequestData);
        } catch (JsonProcessingException ignored) {
        }
        return "";
    }

}
