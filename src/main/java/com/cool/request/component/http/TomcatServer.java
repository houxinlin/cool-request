package com.cool.request.component.http;

public class TomcatServer implements StaticResourceServer {
    private int port;
    private String bastPath;

    public TomcatServer(int port, String bastPath) {
        this.port = port;
        this.bastPath = bastPath;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
