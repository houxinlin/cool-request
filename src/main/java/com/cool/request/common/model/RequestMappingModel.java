package com.cool.request.common.model;

import com.cool.request.common.bean.components.controller.DynamicController;

import java.util.List;

public class RequestMappingModel  extends Model{
    private int pluginPort;
    private DynamicController controller;
    private int serverPort;
    private int total;


    public int getPluginPort() {
        return pluginPort;
    }

    public void setPluginPort(int pluginPort) {
        this.pluginPort = pluginPort;
    }

    public DynamicController getController() {
        return controller;
    }

    public void setController(DynamicController controller) {
        this.controller = controller;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
