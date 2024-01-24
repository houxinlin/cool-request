package com.hxl.plugin.springboot.invoke.script;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.utils.FileUtils;

import java.nio.charset.StandardCharsets;
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
        return invokeResponseModel.getData();
    }

    @Override
    public int getCode() {
        return 0;
    }

    public byte[] getBody() {
        return invokeResponseModel.getData();
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

    public void saveResponseBody(String path) {
        byte[] body = getBody();
        if (body == null) {
            body = new byte[0];
        }
        FileUtils.writeFile(path, body);
    }

    public void save(String path) {
        StringBuilder bodyBuffer = new StringBuilder();
        bodyBuffer.append(invokeResponseModel.headerToString()).append("\n");
        byte[] body = getBody();
        if (body == null) {
            body = new byte[0];
        }
        bodyBuffer.append(new String(body, StandardCharsets.UTF_8));
        FileUtils.writeFile(path, bodyBuffer.toString());
    }
}
