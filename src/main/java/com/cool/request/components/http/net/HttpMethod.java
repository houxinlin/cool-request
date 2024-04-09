/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HttpMethod.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.components.http.net;

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
            if (method instanceof HttpMethod) {
                return (HttpMethod) method;
            }
            return HttpMethod.valueOf(method.toString().toUpperCase(Locale.ROOT));
        } catch (Exception ignore) {
            return GET;
        }
    }
}