package com.cool.request.component.staticServer;

import com.cool.request.utils.StringUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import io.ktor.util.collections.ConcurrentList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service()
public final class StaticResourceServerServiceImpl implements Disposable, StaticResourceServerService {
    public StaticResourceServerServiceImpl() {

    }
    private List<StaticResourceServer> runningServer = Collections.synchronizedList(new ArrayList<>());
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public void start(StaticServer staticServer) {
        threadPoolExecutor.submit(() -> {
            StaticResourceServer staticResourceServer = StaticResourceServerFactory.createStaticResourceServer(staticServer);
            staticResourceServer.start();
            runningServer.add(staticResourceServer);
        });

    }

    @Override
    public void dispose() {

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
