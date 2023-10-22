package com.hxl.plugin.springboot.invoke.model;

import java.util.ArrayList;

public class ErrorInvokeResponseModel extends InvokeResponseModel {

    public ErrorInvokeResponseModel(String msg) {
        super.setData(msg);
        super.setHeader(new ArrayList<>());
    }
}
