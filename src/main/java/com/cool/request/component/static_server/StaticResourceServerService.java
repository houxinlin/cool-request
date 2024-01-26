package com.cool.request.component.static_server;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class StaticResourceServerService {
    public static StaticResourceServerService getInstance() {
        return ApplicationManager.getApplication().getService(StaticResourceServerService.class);
    }

    public void start(StaticServer staticServer) {
        StaticResourceServer staticResourceServer = StaticResourceServerFactory.createStaticResourceServer(staticServer.getPort(), staticServer.getRoot());
        staticResourceServer.start();
    }
}
