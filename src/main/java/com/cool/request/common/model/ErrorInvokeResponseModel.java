package com.cool.request.common.model;

import com.cool.request.utils.Base64Utils;

import java.util.ArrayList;

public class ErrorInvokeResponseModel extends InvokeResponseModel {

    public ErrorInvokeResponseModel(byte[] msg) {
        super.setBase64BodyData(Base64Utils.encodeToString(msg));
        super.setHeader(new ArrayList<>());
        setCode(-1);
    }
}
