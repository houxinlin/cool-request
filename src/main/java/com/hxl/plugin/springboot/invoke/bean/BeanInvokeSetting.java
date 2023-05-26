package com.hxl.plugin.springboot.invoke.bean;

public class BeanInvokeSetting {
    private boolean useProxy;

    private boolean useInterceptor;

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
}
