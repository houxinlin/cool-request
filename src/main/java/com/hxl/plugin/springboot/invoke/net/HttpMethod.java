package com.hxl.plugin.springboot.invoke.net;

import java.util.Arrays;

public enum HttpMethod {

    /**
     * Request
     */
    REQUEST,

    /**
     * GET
     */
    GET,

    /**
     * OPTIONS
     */
    OPTIONS,

    /**
     * POST
     */
    POST,

    /**
     * PUT
     */
    PUT,

    /**
     * DELETE
     */
    DELETE,

    /**
     * PATCH
     */
    PATCH,

    /**
     * HEAD
     */
    HEAD,

    /**
     * TRACE
     */
    TRACE;

    public static HttpMethod[] getValues() {
        return Arrays.stream(HttpMethod.values()).filter(method -> !method.equals(HttpMethod.REQUEST)).toArray(HttpMethod[]::new);
    }

    public static HttpMethod parse( Object method) {
        try {
            assert method != null;
            if (method instanceof HttpMethod) {
                return (HttpMethod) method;
            }
            return HttpMethod.valueOf(method.toString());
        } catch (Exception ignore) {
            return REQUEST;
        }
    }
}