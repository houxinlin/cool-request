package com.cool.request.lib.springmvc;

import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.KeyValue;

import java.util.List;

public class RequestCache {
    private String url;
    private String requestBody;
    private String requestBodyType;
    private String httpMethod;
    private List<KeyValue> headers;
    private List<KeyValue> urlParams;
    private List<KeyValue> urlPathParams;
    private List<FormDataInfo> formDataInfos;
    private List<KeyValue> urlencodedBody;
    private int invokeModelIndex;
    private boolean useProxy;
    private boolean useInterceptor;
    private String contentPath;
    private String requestScript;
    private String responseScript;
    private int port;
    private String scriptLog;

    public String getScriptLog() {
        return scriptLog;
    }

    public void setScriptLog(String scriptLog) {
        this.scriptLog = scriptLog;
    }

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

    public String getRequestScript() {
        return requestScript;
    }

    public void setRequestScript(String requestScript) {
        this.requestScript = requestScript;
    }

    public String getResponseScript() {
        return responseScript;
    }

    public void setResponseScript(String responseScript) {
        this.responseScript = responseScript;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<KeyValue> getUrlPathParams() {
        return urlPathParams;
    }

    public void setUrlPathParams(List<KeyValue> urlPathParams) {
        this.urlPathParams = urlPathParams;
    }

    public static final class RequestCacheBuilder {
        private String url;
        private String requestBody;
        private String requestBodyType;
        private String httpMethod;
        private List<KeyValue> headers;
        private List<KeyValue> urlParams;
        private List<KeyValue> urlPathParams;
        private List<FormDataInfo> formDataInfos;
        private List<KeyValue> urlencodedBody;
        private int invokeModelIndex;
        private boolean useProxy;
        private boolean useInterceptor;
        private String contentPath;
        private int port;
        private String scriptLog;
        private String requestScript;
        private String responseScript;

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
        public RequestCacheBuilder withUrlPathParams(List<KeyValue> pathParams) {
            this.urlPathParams = pathParams;
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

        public RequestCacheBuilder withResponseScript(String script) {
            this.responseScript = script;
            return this;
        }

        public RequestCacheBuilder withRequestScript(String  script) {
            this.requestScript = script;
            return this;
        }
        public RequestCacheBuilder withScriptLog(String  log) {
            this.scriptLog = log;
            return this;
        }
        public RequestCacheBuilder withHttpMethod(String  httpMethod) {
            this.httpMethod = httpMethod;
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
            requestCache.setUrlPathParams(urlPathParams);
            requestCache.setScriptLog(scriptLog);
            requestCache.setRequestScript(requestScript);
            requestCache.setResponseScript(responseScript);
            requestCache.setHttpMethod(httpMethod);
            return requestCache;
        }
    }
}
