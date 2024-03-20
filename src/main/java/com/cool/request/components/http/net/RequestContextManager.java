package com.cool.request.components.http.net;

import com.cool.request.components.http.Controller;

import java.util.HashMap;
import java.util.Map;

public class RequestContextManager {
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
