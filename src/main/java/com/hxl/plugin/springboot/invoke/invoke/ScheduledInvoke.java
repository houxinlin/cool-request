package com.hxl.plugin.springboot.invoke.invoke;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.bean.ScheduleInvokeRequestBody;

public class ScheduledInvoke extends BaseProjectInvoke<ScheduledInvoke.InvokeData> {
    public ScheduledInvoke(int port) {
        super(port);
    }
    @Override
    public String createMessage(InvokeData invokeData) {
        try {
            return new ObjectMapper().writeValueAsString(new ScheduleInvokeRequestBody(invokeData.getId()));
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
