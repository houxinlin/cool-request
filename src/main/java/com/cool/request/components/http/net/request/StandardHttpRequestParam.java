/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StandardHttpRequestParam.java is part of Cool Request
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

import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.components.http.KeyValue;
import com.cool.request.lib.springmvc.Body;

import java.util.ArrayList;
import java.util.List;

public class StandardHttpRequestParam extends HttpRequestParam {
    private String url;  //url路径
    private final List<KeyValue> headers = new ArrayList<>(); //请求头
    private final List<KeyValue> urlParam = new ArrayList<>(); //url参数
    private final List<KeyValue> pathParam = new ArrayList<>(); //url参数
    private Body body; //post body
    private HttpMethod method; //http方法

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<KeyValue> getPathParam() {
        return pathParam;
    }

    public List<KeyValue> getHeaders() {
        return headers;
    }

    public List<KeyValue> getUrlParam() {
        return urlParam;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
