package com.cool.request.component.static_server;

import com.cool.request.utils.StringUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import io.ktor.util.collections.ConcurrentList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public final class StaticResourceServerService {
    private List<StaticResourceServer> runningServer = new ConcurrentList<>();

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public static StaticResourceServerService getInstance() {
        return ApplicationManager.getApplication().getService(StaticResourceServerService.class);
    }

    public void start(StaticServer staticServer) {
        threadPoolExecutor.submit(() -> {
            StaticResourceServer staticResourceServer = StaticResourceServerFactory.createStaticResourceServer(staticServer);
            staticResourceServer.start();
            runningServer.add(staticResourceServer);
        });

    }

    public void stop(StaticServer staticServer) {
        Iterator<StaticResourceServer> iterator = runningServer.iterator();
        while (iterator.hasNext()) {
            StaticResourceServer staticResourceServer = iterator.next();
            if (StringUtils.isEqualsIgnoreCase(staticResourceServer.getId(), staticServer.getId())) {
                staticResourceServer.stop();
                iterator.remove();
            }
        }
    }

    public boolean isRunning(StaticServer staticServer) {
        return runningServer.stream()
                .filter(staticResourceServer ->
                        StringUtils.isEqualsIgnoreCase(staticResourceServer.getId(), staticServer.getId())).count() > 0;
    }
}
