package com.cool.request.common.model;

import java.util.ArrayList;

public class ErrorInvokeResponseModel extends InvokeResponseModel {

    public ErrorInvokeResponseModel(byte[] msg) {
        super.setData(msg);
        super.setHeader(new ArrayList<>());
        setCode(-1);
    }
}
