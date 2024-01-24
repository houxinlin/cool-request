package com.cool.request.net;

import com.cool.request.invoke.InvokeException;
import com.cool.request.net.request.StandardHttpRequestParam;

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
