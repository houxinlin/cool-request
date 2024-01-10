package com.hxl.plugin.springboot.invoke.model;

public class ProjectStartupModel  extends Model{
    public ProjectStartupModel(int port) {
        this.port = port;
    }
    public ProjectStartupModel() {
    }

    public ProjectStartupModel(int port, int webPort) {
        this.port = port;
        this.webPort = webPort;
    }

    private int port;
    private  int webPort;

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
