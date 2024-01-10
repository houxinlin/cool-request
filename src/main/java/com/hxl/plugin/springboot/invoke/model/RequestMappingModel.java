package com.hxl.plugin.springboot.invoke.model;

import com.hxl.plugin.springboot.invoke.bean.components.controller.DynamicController;

import java.util.List;

public class RequestMappingModel  extends Model{
    private int pluginPort;
    private List<DynamicController> controllers;
    private int serverPort;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getPluginPort() {
        return pluginPort;
    }

    public void setPluginPort(int pluginPort) {
        this.pluginPort = pluginPort;
    }
    public List<DynamicController> getControllers() {
        return controllers;
    }

    public void setControllers(List<DynamicController> controllers) {
        this.controllers = controllers;
    }
}
