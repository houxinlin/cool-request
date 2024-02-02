package com.cool.request.component.http.invoke;

import com.cool.request.common.bean.ScheduleInvokeRequestBody;
import com.cool.request.utils.GsonUtils;

public class ScheduledComponentRequest extends BasicRemoteComponentRequest<ScheduledComponentRequest.InvokeData> {
    public ScheduledComponentRequest(int port) {
        super(port);
    }

    @Override
    public String createMessage(InvokeData invokeData) {
        return GsonUtils.toJsonString(new ScheduleInvokeRequestBody(invokeData.getId()));
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
