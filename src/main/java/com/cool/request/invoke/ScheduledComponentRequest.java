package com.cool.request.invoke;

import com.cool.request.bean.ScheduleInvokeRequestBody;
import com.cool.request.utils.ObjectMappingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ScheduledComponentRequest extends BasicRemoteComponentRequest<ScheduledComponentRequest.InvokeData> {
    public ScheduledComponentRequest(int port) {
        super(port);
    }
    @Override
    public String createMessage(InvokeData invokeData) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(new ScheduleInvokeRequestBody(invokeData.getId()));
        } catch (JsonProcessingException ignored) {

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
