package com.cool.request.components.staticServer;

public interface StaticResourceServer {
    public String getId();

    public void start();

    public void stop();

    public void setListDir(boolean listDir);
}
