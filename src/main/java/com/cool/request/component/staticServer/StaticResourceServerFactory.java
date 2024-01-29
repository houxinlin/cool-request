package com.cool.request.component.staticServer;

public class StaticResourceServerFactory {
    public static StaticResourceServer createStaticResourceServer(StaticServer staticServer) {
        return new TomcatServer(staticServer);
    }
}
