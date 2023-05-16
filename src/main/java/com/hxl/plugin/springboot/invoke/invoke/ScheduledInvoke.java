package com.hxl.plugin.springboot.invoke.invoke;

import com.google.gson.Gson;
import com.hxl.plugin.springboot.invoke.bean.ScheduleInvokeRequestBody;

public class ScheduledInvoke extends BaseProjectInvoke<ScheduledInvoke.InvokeData> {
    public ScheduledInvoke(int port) {
        super(port);
    }
    @Override
    public String createMessage(InvokeData invokeData) {
        return new Gson().toJson(new ScheduleInvokeRequestBody(invokeData.getId()));
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
