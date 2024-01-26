package com.cool.request.component.static_server;

public class StaticServer {
    private String id; //uuid
    private int port;
    private String root;

    public StaticServer(String id, int port, String root) {
        this.id = id;
        this.port = port;
        this.root = root;
    }

    public StaticServer() {
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
