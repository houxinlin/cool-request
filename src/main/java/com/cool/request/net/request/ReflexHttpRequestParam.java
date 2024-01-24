package com.cool.request.net.request;

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
