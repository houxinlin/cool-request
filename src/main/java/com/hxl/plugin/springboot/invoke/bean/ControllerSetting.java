package com.hxl.plugin.springboot.invoke.bean;

public class ControllerSetting {
    private String url;
    private String body;
    private Boolean useProxy;
    private Boolean useInterceptor;
    private Boolean jsonContent;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getUseProxy() {
        return useProxy;
    }

    public void setUseProxy(Boolean useProxy) {
        this.useProxy = useProxy;
    }

    public Boolean getUseInterceptor() {
        return useInterceptor;
    }

    public void setUseInterceptor(Boolean useInterceptor) {
        this.useInterceptor = useInterceptor;
    }

    public Boolean getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(Boolean jsonContent) {
        this.jsonContent = jsonContent;
    }
}
