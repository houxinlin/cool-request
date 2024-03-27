/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestContextManager.java is part of Cool Request
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
