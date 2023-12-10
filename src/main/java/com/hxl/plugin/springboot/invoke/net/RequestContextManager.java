package com.hxl.plugin.springboot.invoke.net;

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
}
