package com.cool.request.component.static_server;

public class StaticResourceServerFactory {
    public static StaticResourceServer createStaticResourceServer(int port, String path) {
        return new TomcatServer(port, path);
    }
}
