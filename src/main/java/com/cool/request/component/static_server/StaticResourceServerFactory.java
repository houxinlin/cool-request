package com.cool.request.component.static_server;

public class StaticResourceServerFactory {
    public static StaticResourceServer createStaticResourceServer(StaticServer staticServer) {
        return new TomcatServer(staticServer);
    }
}
