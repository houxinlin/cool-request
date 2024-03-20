package com.cool.request.components.staticServer;

public interface StaticResourceServerService {
    public void stop(StaticServer staticServer);

    public boolean isRunning(StaticServer staticServer);

    public void start(StaticServer staticServer);

    public StaticResourceServer getStaticServerIfRunning(StaticServer staticServer);
}
