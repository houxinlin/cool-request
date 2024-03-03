package com.cool.request.component.http;

import com.cool.request.component.http.net.HTTPResponseBody;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public final class ReflexRequestResponseListenerMapMap implements Disposable {
    private final Map<Long, List<HTTPResponseListener>> listMap = new HashMap<>();
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;

    public ReflexRequestResponseListenerMapMap() {

        scheduledFuture = executor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            synchronized (listMap) {
                listMap.entrySet().removeIf(entry -> (currentTime - entry.getKey()) > 10 * 60 * 1000);
            }
        }, 0, 10, TimeUnit.MINUTES);

    }

    @Override
    public void dispose() {
        scheduledFuture.cancel(false);
    }

    public void register(Long time, List<HTTPResponseListener> httpResponseListener) {
        listMap.computeIfAbsent(time, aLong -> new ArrayList<>()).addAll(httpResponseListener);
    }

    public void notifyResponse(Long time, String requestId, HTTPResponseBody httpResponseBody) {
        for (HTTPResponseListener httpResponseListener : listMap.computeIfAbsent(time, aLong -> new ArrayList<>())) {
            httpResponseListener.onResponseEvent(requestId, httpResponseBody);
        }
        listMap.remove(time);
    }

    public static ReflexRequestResponseListenerMapMap getInstance(Project project) {
        return project.getService(ReflexRequestResponseListenerMapMap.class);
    }
}
