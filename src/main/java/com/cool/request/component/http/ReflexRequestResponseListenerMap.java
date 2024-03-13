package com.cool.request.component.http;

import com.cool.request.component.http.net.HTTPResponseBody;
import com.cool.request.component.http.net.RequestContext;
import com.cool.request.view.main.HTTPEventListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public final class ReflexRequestResponseListenerMap implements Disposable {
    private final Map<Long, RequestContext> requestContextMap = new HashMap<>();
    private final ScheduledFuture<?> scheduledFuture;

    public ReflexRequestResponseListenerMap() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        scheduledFuture = executor.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            synchronized (requestContextMap) {
                requestContextMap.entrySet().removeIf(entry -> (currentTime - entry.getKey()) > 10 * 60 * 1000);
            }
        }, 0, 10, TimeUnit.MINUTES);

    }

    @Override
    public void dispose() {
        scheduledFuture.cancel(false);
    }

    public void register(Long time, RequestContext requestContext) {
        requestContextMap.put(time, requestContext);
    }

    public void notifyResponse(Long time, HTTPResponseBody httpResponseBody) {
        RequestContext requestContext = requestContextMap.get(time);
        if (requestContext == null) return;
        for (HTTPEventListener httpEventListener : requestContextMap.get(time).getHttpEventListeners()) {
            httpEventListener.endSend(requestContext, httpResponseBody);
        }
        requestContextMap.remove(time);
    }

    public static ReflexRequestResponseListenerMap getInstance(Project project) {
        return project.getService(ReflexRequestResponseListenerMap.class);
    }
}
