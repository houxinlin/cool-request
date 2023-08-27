package com.hxl.plugin.springboot.invoke.springmvc;

import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;

import java.util.List;
import java.util.Map;

public class RequestCache {
    private String url;
    private String requestBody;
    private String requestBodyType;
    private List<KeyValue> headers ;
    private List<KeyValue> urlParams ;
    private List<FormDataInfo> formDataInfos;
    private List<KeyValue> urlencodedBody;
    private int invokeModelIndex;
    private boolean useProxy;
    private boolean useInterceptor;
    private String contentPath;
    private int port;

    public List<KeyValue> getHeaders() {
        return headers;
    }

    public void setHeaders(List<KeyValue> headers) {
        this.headers = headers;
    }

    public List<KeyValue> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(List<KeyValue> urlParams) {
        this.urlParams = urlParams;
    }

    public List<FormDataInfo> getFormDataInfos() {
        return formDataInfos;
    }

    public void setFormDataInfos(List<FormDataInfo> formDataInfos) {
        this.formDataInfos = formDataInfos;
    }

    public List<KeyValue> getUrlencodedBody() {
        return urlencodedBody;
    }

    public void setUrlencodedBody(List<KeyValue> urlencodedBody) {
        this.urlencodedBody = urlencodedBody;
    }


    public String getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(String requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private Map<String,Object> requestHeader;

    public Map<String, Object> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, Object> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public int getInvokeModelIndex() {
        return invokeModelIndex;
    }

    public void setInvokeModelIndex(int invokeModelIndex) {
        this.invokeModelIndex = invokeModelIndex;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public boolean isUseInterceptor() {
        return useInterceptor;
    }

    public void setUseInterceptor(boolean useInterceptor) {
        this.useInterceptor = useInterceptor;
    }


    public static final class RequestCacheBuilder {
        private String url;
        private String requestBody;
        private String requestBodyType;
        private List<KeyValue> headers;
        private List<KeyValue> urlParams;
        private List<FormDataInfo> formDataInfos;
        private List<KeyValue> urlencodedBody;
        private String textBody;
        private int invokeModelIndex;
        private boolean useProxy;
        private boolean useInterceptor;
        private String contentPath;
        private int port;

        private RequestCacheBuilder() {
        }

        public static RequestCacheBuilder aRequestCache() {
            return new RequestCacheBuilder();
        }

        public RequestCacheBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public RequestCacheBuilder withRequestBody(String requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public RequestCacheBuilder withRequestBodyType(String requestBodyType) {
            this.requestBodyType = requestBodyType;
            return this;
        }

        public RequestCacheBuilder withHeaders(List<KeyValue> headers) {
            this.headers = headers;
            return this;
        }

        public RequestCacheBuilder withUrlParams(List<KeyValue> urlParams) {
            this.urlParams = urlParams;
            return this;
        }

        public RequestCacheBuilder withFormDataInfos(List<FormDataInfo> formDataInfos) {
            this.formDataInfos = formDataInfos;
            return this;
        }

        public RequestCacheBuilder withUrlencodedBody(List<KeyValue> urlencodedBody) {
            this.urlencodedBody = urlencodedBody;
            return this;
        }

        public RequestCacheBuilder withTextBody(String textBody) {
            this.textBody = textBody;
            return this;
        }

        public RequestCacheBuilder withInvokeModelIndex(int invokeModelIndex) {
            this.invokeModelIndex = invokeModelIndex;
            return this;
        }

        public RequestCacheBuilder withUseProxy(boolean useProxy) {
            this.useProxy = useProxy;
            return this;
        }

        public RequestCacheBuilder withUseInterceptor(boolean useInterceptor) {
            this.useInterceptor = useInterceptor;
            return this;
        }

        public RequestCacheBuilder withContentPath(String contentPath) {
            this.contentPath = contentPath;
            return this;
        }

        public RequestCacheBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        public RequestCache build() {
            RequestCache requestCache = new RequestCache();
            requestCache.setUrl(url);
            requestCache.setRequestBody(requestBody);
            requestCache.setRequestBodyType(requestBodyType);
            requestCache.setHeaders(headers);
            requestCache.setUrlParams(urlParams);
            requestCache.setFormDataInfos(formDataInfos);
            requestCache.setUrlencodedBody(urlencodedBody);
            requestCache.setInvokeModelIndex(invokeModelIndex);
            requestCache.setUseProxy(useProxy);
            requestCache.setUseInterceptor(useInterceptor);
            requestCache.setContentPath(contentPath);
            requestCache.setPort(port);
            return requestCache;
        }
    }
}
