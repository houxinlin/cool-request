package com.cool.request.component.http.net;

import java.util.Arrays;
import java.util.Locale;

public enum HttpMethod {

    /**
     * Request
     */
    ALL,

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
        return Arrays.stream(HttpMethod.values()).filter(method -> !method.equals(HttpMethod.ALL)).toArray(HttpMethod[]::new);
    }

    public static HttpMethod parse(Object method) {
        try {
            assert method != null;
            if (method instanceof HttpMethod) {
                return (HttpMethod) method;
            }
            return HttpMethod.valueOf(method.toString().toUpperCase(Locale.ROOT));
        } catch (Exception ignore) {
            return ALL;
        }
    }
}