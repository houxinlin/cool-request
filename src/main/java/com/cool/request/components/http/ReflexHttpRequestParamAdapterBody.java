/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ReflexHttpRequestParamAdapterBody.java is part of Cool Request
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

package com.cool.request.components.http;

import java.util.ArrayList;
import java.util.List;

/**
 * 发生调用时候发起的数据包，将来把这里优化掉
 */
public class ReflexHttpRequestParamAdapterBody extends ReflexRequestBody {
    private static final long serialVersionUID = 1000000;
    private String url;
    private String contentType;
    private List<FormDataInfo> formData = new ArrayList<>();
    private String body; //json xml raw bin urlencoded
    private boolean useProxyObject;
    private boolean useInterceptor;
    private boolean userFilter;
    private List<KeyValue> headers = new ArrayList<>();
    private String method;
    private Object attachData; //附加数据

    public ReflexHttpRequestParamAdapterBody() {
    }

    public Object getAttachData() {
        return attachData;
    }

    public void setAttachData(Object attachData) {
        this.attachData = attachData;
    }

    @Override
    public String getType() {
        return "controller";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<FormDataInfo> getFormData() {
        return formData;
    }

    public void setFormData(List<FormDataInfo> formData) {
        this.formData = formData;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public List<KeyValue> getHeaders() {
        return headers;
    }

    public void setHeaders(List<KeyValue> headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


    public static final class ReflexHttpRequestParamAdapterBuilder {
        private String url;
        private String contentType;
        private List<FormDataInfo> formData;
        private String body;
        private String id;
        private boolean useProxyObject;
        private boolean useInterceptor;
        private boolean userFilter;
        private List<KeyValue> headers;
        private String method;

        private ReflexHttpRequestParamAdapterBuilder() {
        }

        public static ReflexHttpRequestParamAdapterBuilder aReflexHttpRequestParamAdapter() {
            return new ReflexHttpRequestParamAdapterBuilder();
        }

        public ReflexHttpRequestParamAdapterBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withFormData(List<FormDataInfo> formData) {
            this.formData = formData;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withUseProxyObject(boolean useProxyObject) {
            this.useProxyObject = useProxyObject;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withUseInterceptor(boolean useInterceptor) {
            this.useInterceptor = useInterceptor;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withUserFilter(boolean userFilter) {
            this.userFilter = userFilter;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withHeaders(List<KeyValue> headers) {
            this.headers = headers;
            return this;
        }

        public ReflexHttpRequestParamAdapterBuilder withMethod(String method) {
            this.method = method;
            return this;
        }

        public ReflexHttpRequestParamAdapterBody build() {
            ReflexHttpRequestParamAdapterBody reflexHttpRequestParamAdapter = new ReflexHttpRequestParamAdapterBody();
            reflexHttpRequestParamAdapter.setUrl(url);
            reflexHttpRequestParamAdapter.setContentType(contentType);
            reflexHttpRequestParamAdapter.setFormData(formData);
            reflexHttpRequestParamAdapter.setBody(body);
            reflexHttpRequestParamAdapter.setId(id);
            reflexHttpRequestParamAdapter.setUseProxyObject(useProxyObject);
            reflexHttpRequestParamAdapter.setUseInterceptor(useInterceptor);
            reflexHttpRequestParamAdapter.setUserFilter(userFilter);
            reflexHttpRequestParamAdapter.setHeaders(headers);
            reflexHttpRequestParamAdapter.setMethod(method);
            return reflexHttpRequestParamAdapter;
        }
    }
}
