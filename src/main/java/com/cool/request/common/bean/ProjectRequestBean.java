/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ProjectRequestBean.java is part of Cool Request
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

package com.cool.request.common.bean;


import com.cool.request.common.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.cool.request.common.model.SpringScheduledSpringInvokeEndpoint;

import java.util.List;
import java.util.Map;

public class ProjectRequestBean {
    private String type;
    private String response;
    private List<SpringMvcRequestMappingSpringInvokeEndpoint> controller;
    private List<SpringScheduledSpringInvokeEndpoint> scheduled;
    private int port;
    private int serverPort;
    private String contextPath;
    private String id;
    private Map<String, List<String>> responseHeaders;

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }
    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getType() {
        return type;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SpringMvcRequestMappingSpringInvokeEndpoint> getController() {
        return controller;
    }

    public void setController(List<SpringMvcRequestMappingSpringInvokeEndpoint> controller) {
        this.controller = controller;
    }

    public List<SpringScheduledSpringInvokeEndpoint> getScheduled() {
        return scheduled;
    }

    public void setScheduled(List<SpringScheduledSpringInvokeEndpoint> scheduled) {
        this.scheduled = scheduled;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
