package com.hxl.plugin.springboot.invoke.script;

import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.utils.FileUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Response {
    private final InvokeResponseModel invokeResponseModel;

    public Response(InvokeResponseModel invokeResponseModel) {
        this.invokeResponseModel = invokeResponseModel;
    }

    public byte[] getBody() {
        return invokeResponseModel.getData();
    }

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

    public Set<String> getHeaderKeys() {
        if (invokeResponseModel.getHeader() == null) {
            return new HashSet<>();
        }
        return invokeResponseModel.getHeader().stream().map(InvokeResponseModel.Header::getKey).collect(Collectors.toSet());
    }


    public void saveResponseBody(String path) {
        byte[] body = getBody();
        if (body == null) {
            body = new byte[]{0};
        }
        FileUtils.writeFile(path, body);
    }

    public void save(String path) {
        StringBuilder bodyBuffer = new StringBuilder();
        bodyBuffer.append(getHeaderAsString()).append("\n");
        byte[] body = getBody();
        if (body == null) {
            body = new byte[]{0};
        }
        bodyBuffer.append(new String(body, StandardCharsets.UTF_8));
        FileUtils.writeFile(path, bodyBuffer.toString());
    }

    protected String getId() {
        return invokeResponseModel.getId();
    }

    public String getHeaderAsString() {
        return invokeResponseModel.headerToString();
    }
}
