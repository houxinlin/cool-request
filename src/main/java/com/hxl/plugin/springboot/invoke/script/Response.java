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
    public List<String> getHeaders(String key) {
        if (invokeResponseModel.getHeader() == null) return new ArrayList<>();
        return invokeResponseModel.getHeader().stream().map(InvokeResponseModel.Header::getValue).collect(Collectors.toList());
    }
    public String getHeader(String key) {
        List<String> allHeader = getHeaders(key);
        if (allHeader.isEmpty()) return null;
        return allHeader.get(0);
    }

    protected String getId(){
        return invokeResponseModel.getId();
    }
    public String getHeaderAsString() {
        return invokeResponseModel.headerToString();
    }
}
