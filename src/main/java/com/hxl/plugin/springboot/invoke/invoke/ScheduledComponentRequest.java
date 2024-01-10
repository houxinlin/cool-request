package com.hxl.plugin.springboot.invoke.invoke;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxl.plugin.springboot.invoke.bean.ScheduleInvokeRequestBody;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;

public class ScheduledComponentRequest extends BasicRemoteComponentRequest<ScheduledComponentRequest.InvokeData> {
    public ScheduledComponentRequest(int port) {
        super(port);
    }
    @Override
    public String createMessage(InvokeData invokeData) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(new ScheduleInvokeRequestBody(invokeData.getId()));
        } catch (JsonProcessingException e) {

        }
        return "";
    }
    public static class InvokeData {
        private String id;

        public InvokeData(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
