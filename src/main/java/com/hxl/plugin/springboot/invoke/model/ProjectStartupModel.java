package com.hxl.plugin.springboot.invoke.model;

public class ProjectStartupModel  extends Model{
    public ProjectStartupModel(int port) {
        this.port = port;
    }
    public ProjectStartupModel() {
    }

    private int port;
    private  int projectPort;

    public int getProjectPort() {
        return projectPort;
    }

    public void setProjectPort(int projectPort) {
        this.projectPort = projectPort;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
