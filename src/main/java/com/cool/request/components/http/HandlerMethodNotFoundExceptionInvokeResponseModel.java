package com.cool.request.components.http;

import java.io.Serializable;

public class HandlerMethodNotFoundExceptionInvokeResponseModel extends ExceptionInvokeResponseModel implements Serializable {
    private static final long serialVersionUID = 1000000;

    public HandlerMethodNotFoundExceptionInvokeResponseModel(String id, Exception e) {
        super(id, e);
    }
}
