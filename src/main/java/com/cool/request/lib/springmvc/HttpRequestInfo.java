/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HttpRequestInfo.java is part of Cool Request
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

package com.cool.request.lib.springmvc;

import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.RequestParameterDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * http的请求信息，用来给参数推测使用
 */
public class HttpRequestInfo {
    private UrlInfo url;
    private GuessBody requestBody;
    private GuessBody responseBody;
    private String contentType;
    private List<RequestParameterDescription> headers = new ArrayList<>();
    private List<RequestParameterDescription> urlParams = new ArrayList<>();
    private List<RequestParameterDescription> pathParams = new ArrayList<>();
    private List<FormDataInfo> formDataInfos = new ArrayList<>();
    private List<RequestParameterDescription> urlencodedBody = new ArrayList<>();

    public GuessBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(GuessBody requestBody) {
        this.requestBody = requestBody;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<RequestParameterDescription> getHeaders() {
        return headers;
    }

    public void setHeaders(List<RequestParameterDescription> headers) {
        this.headers = headers;
    }

    public List<RequestParameterDescription> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(List<RequestParameterDescription> urlParams) {
        this.urlParams = urlParams;
    }

    public List<FormDataInfo> getFormDataInfos() {
        return formDataInfos;
    }

    public void setFormDataInfos(List<FormDataInfo> formDataInfos) {
        this.formDataInfos = formDataInfos;
    }

    public List<RequestParameterDescription> getUrlencodedBody() {
        return urlencodedBody;
    }

    public void setUrlencodedBody(List<RequestParameterDescription> urlencodedBody) {
        this.urlencodedBody = urlencodedBody;
    }

    public List<RequestParameterDescription> getPathParams() {
        return pathParams;
    }

    public void setPathParams(List<RequestParameterDescription> pathParams) {
        this.pathParams = pathParams;
    }

    public UrlInfo getUrl() {
        return url;
    }

    public void setUrl(UrlInfo url) {
        this.url = url;
    }

    public GuessBody getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(GuessBody responseBody) {
        this.responseBody = responseBody;
    }

    public static class UrlInfo {
        private String url;
        private List<String> pathVariable;

        public UrlInfo(String url) {
            this(url, new ArrayList<>());
        }

        public UrlInfo(String url, List<String> pathVariable) {
            this.url = url;
            this.pathVariable = pathVariable;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<String> getPathVariable() {
            return pathVariable;
        }

        public void setPathVariable(List<String> pathVariable) {
            this.pathVariable = pathVariable;
        }
    }
}
