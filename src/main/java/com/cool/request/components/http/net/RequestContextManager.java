package com.cool.request.components.http.net;

import com.cool.request.components.http.Controller;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

@Service
public final class RequestContextManager {
    public static RequestContextManager getInstance(Project project) {
        return project.getService(RequestContextManager.class);
    }

    private final Map<String, RequestContext> requestContextMap = new HashMap<>();

    public void put(String id, RequestContext requestContext) {
        this.requestContextMap.put(id, requestContext);
    }

    public RequestContext get(String id) {
        return this.requestContextMap.get(id);
    }

    public Controller getCurrentController(String id) {
        if (!requestContextMap.containsKey(id)) return null;
        RequestContext requestContext = requestContextMap.get(id);
        return requestContext.getController();
    }
}
