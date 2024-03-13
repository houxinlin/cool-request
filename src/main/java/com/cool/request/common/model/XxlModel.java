package com.cool.request.common.model;

import com.cool.request.common.bean.components.scheduled.DynamicXxlJobScheduled;
import com.cool.request.common.bean.components.scheduled.XxlJobScheduled;

import java.util.List;

public class XxlModel extends Model {
    private List<DynamicXxlJobScheduled> xxlJobInvokeEndpoint;
    private int serverPort = 0;

    public List<DynamicXxlJobScheduled> getXxlJobInvokeEndpoint() {
        return xxlJobInvokeEndpoint;
    }

    public void setXxlJobInvokeEndpoint(List<DynamicXxlJobScheduled> xxlJobInvokeEndpoint) {
        this.xxlJobInvokeEndpoint = xxlJobInvokeEndpoint;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
