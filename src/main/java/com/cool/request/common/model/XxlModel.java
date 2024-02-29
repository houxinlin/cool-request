package com.cool.request.common.model;

import com.cool.request.common.bean.components.xxljob.XxlJob;

import java.util.List;

public class XxlModel  extends Model{
    private List<XxlJob> xxlJobInvokeEndpoint;
    private int serverPort = 0;

    public List<XxlJob> getXxlJobInvokeEndpoint() {
        return xxlJobInvokeEndpoint;
    }

    public void setXxlJobInvokeEndpoint(List<XxlJob> xxlJobInvokeEndpoint) {
        this.xxlJobInvokeEndpoint = xxlJobInvokeEndpoint;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
