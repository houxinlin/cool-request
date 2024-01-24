package com.cool.request.model;

public class ProjectStartupModel  extends Model{
    private int projectPort;
    public int port;

    public ProjectStartupModel(int projectPort, int port) {
        this.projectPort = projectPort;
        this.port = port;
    }

    public ProjectStartupModel() {
    }

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
