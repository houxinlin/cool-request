package com.hxl.plugin.springboot.invoke.model;

public class ProjectStartupModel  extends Model{
    public ProjectStartupModel(int port) {
        this.port = port;
    }

    public ProjectStartupModel() {
    }

    public int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
