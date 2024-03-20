package com.cool.request.components.staticServer;

public class StaticServer {
    private String id; //uuid
    private int port;
    private String root;
    private boolean listDir;
    public StaticServer(String id, int port, String root) {
        this.id = id;
        this.port = port;
        this.root = root;
    }

    public StaticServer() {
    }

    public boolean isListDir() {
        return listDir;
    }

    public void setListDir(boolean listDir) {
        this.listDir = listDir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
