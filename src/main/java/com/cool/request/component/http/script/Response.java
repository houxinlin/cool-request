package com.cool.request.component.http.script;

import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.script.HTTPResponse;
import com.cool.request.utils.Base64Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Response implements HTTPResponse {
    private final InvokeResponseModel invokeResponseModel;

    public Response(InvokeResponseModel invokeResponseModel) {
        this.invokeResponseModel = invokeResponseModel;
    }

    @Override
    public byte[] getResponseBody() {
        return Base64Utils.decode(invokeResponseModel.getBase64BodyData());
    }

    @Override
    public int getCode() {
        return invokeResponseModel.getCode();
    }


    @Override
    public String getHeader(String key) {
        if (invokeResponseModel.getHeader() == null) {
            return null;
        }
        List<String> headers = getHeaders(key);
        if (!headers.isEmpty()) {
            return headers.get(0);
        }
        return null;
    }

    @Override
    public List<String> getHeaders(String key) {
        if (invokeResponseModel.getHeader() == null) {
            return new ArrayList<>();
        }
        return invokeResponseModel.getHeader().stream().filter(header -> key.equalsIgnoreCase(header.getKey()))
                .map(InvokeResponseModel.Header::getValue).collect(Collectors.toList());
    }

    @Override
    public List<String> getHeaderKeys() {
        if (invokeResponseModel.getHeader() == null) {
            return new ArrayList<>();
        }
        return invokeResponseModel.getHeader().stream().map(InvokeResponseModel.Header::getKey).collect(Collectors.toList());
    }

}
