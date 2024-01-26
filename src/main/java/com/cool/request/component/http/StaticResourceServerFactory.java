package com.cool.request.component.http;

public class StaticResourceServerFactory {
    public static StaticResourceServer createStaticResourceServer(int port, String path) {
        return new TomcatServer(port, path);
    }
}
