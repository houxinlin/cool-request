/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ReflexHttpRequestParam.java is part of Cool Request
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

package com.cool.request.components.http.net.request;

/**
 * 最终HTTP请求参数承载类
 */
public class ReflexHttpRequestParam extends StandardHttpRequestParam {
    private final String type = "controller";
    private boolean useProxyObject;
    private boolean useInterceptor;
    private boolean userFilter;

    public ReflexHttpRequestParam(boolean useProxyObject, boolean useInterceptor, boolean userFilter) {
        this.useProxyObject = useProxyObject;
        this.useInterceptor = useInterceptor;
        this.userFilter = userFilter;
    }

    public String getType() {
        return type;
    }

    public boolean isUseProxyObject() {
        return useProxyObject;
    }

    public void setUseProxyObject(boolean useProxyObject) {
        this.useProxyObject = useProxyObject;
    }

    public boolean isUseInterceptor() {
        return useInterceptor;
    }

    public void setUseInterceptor(boolean useInterceptor) {
        this.useInterceptor = useInterceptor;
    }

    public boolean isUserFilter() {
        return userFilter;
    }

    public void setUserFilter(boolean userFilter) {
        this.userFilter = userFilter;
    }

}
