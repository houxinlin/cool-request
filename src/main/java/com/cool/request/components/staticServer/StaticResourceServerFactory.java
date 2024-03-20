package com.cool.request.components.staticServer;

public class StaticResourceServerFactory {
    public static StaticResourceServer createStaticResourceServer(StaticServer staticServer) {
        return new TomcatServer(staticServer);
    }
}
