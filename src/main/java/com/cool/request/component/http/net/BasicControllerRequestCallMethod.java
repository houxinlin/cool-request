package com.cool.request.component.http.net;

import com.cool.request.component.http.invoke.InvokeException;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;

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

    public abstract void invoke() throws InvokeException;
}
