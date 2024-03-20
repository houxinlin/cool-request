package com.cool.request.components.http.net;

import com.cool.request.components.http.invoke.InvokeException;
import com.cool.request.components.http.net.request.StandardHttpRequestParam;

/**
 * 请求发起的方式，http，或者反射
 */
public abstract class BasicControllerRequestCallMethod {
    private final StandardHttpRequestParam reflexHttpRequestParam;

    public BasicControllerRequestCallMethod(StandardHttpRequestParam reflexHttpRequestParam) {
        this.reflexHttpRequestParam = reflexHttpRequestParam;
    }

    public StandardHttpRequestParam getInvokeData() {
        return reflexHttpRequestParam;
    }

    public abstract void invoke(RequestContext requestContext) throws InvokeException;
}
