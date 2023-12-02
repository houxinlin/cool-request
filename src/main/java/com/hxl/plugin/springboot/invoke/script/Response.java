package com.hxl.plugin.springboot.invoke.script;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Response {
    private final InvokeResponseModel invokeResponseModel;

    public Response(InvokeResponseModel invokeResponseModel) {
        this.invokeResponseModel = invokeResponseModel;
    }

    public byte[] getBody() {
        return invokeResponseModel.getData();
    }

    public List<InvokeResponseModel.Header> getHeader() {
        return invokeResponseModel.getHeader();
    }

    public String[] getAllHeader(String key) {
        if (invokeResponseModel.getHeader() == null) return new String[0];
        return invokeResponseModel.getHeader().stream().map(InvokeResponseModel.Header::getValue).collect(Collectors.toList()).toArray(new String[]{});
    }

    public String getHeader(String key) {
        String[] allHeader = getAllHeader(key);
        if (allHeader.length == 0) return null;
        return allHeader[0];
    }

    public String getHeaderAsString() {
        return invokeResponseModel.headerToString();
    }
}
