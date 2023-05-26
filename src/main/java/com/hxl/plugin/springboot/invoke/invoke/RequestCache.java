package com.hxl.plugin.springboot.invoke.invoke;

import java.util.Map;

public class RequestCache {
    private String url;
    private String requestBody;
    private int invokeModelIndex;
    private boolean useProxy;
    private boolean useInterceptor;
    private String contentPath;
    private int port;

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
        private int invokeModelIndex;
        private boolean useProxy;
        private boolean useInterceptor;
        private String contentPath;
        private int port;
        private Map<String, Object> requestHeader;

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

        public RequestCacheBuilder withRequestHeader(Map<String, Object> requestHeader) {
            this.requestHeader = requestHeader;
            return this;
        }

        public RequestCache build() {
            RequestCache requestCache = new RequestCache();
            requestCache.setUrl(url);
            requestCache.setRequestBody(requestBody);
            requestCache.setInvokeModelIndex(invokeModelIndex);
            requestCache.setUseProxy(useProxy);
            requestCache.setUseInterceptor(useInterceptor);
            requestCache.setContentPath(contentPath);
            requestCache.setPort(port);
            requestCache.setRequestHeader(requestHeader);
            return requestCache;
        }
    }
}
