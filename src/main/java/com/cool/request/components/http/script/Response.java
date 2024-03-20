package com.cool.request.components.http.script;

import com.cool.request.components.http.Header;
import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.script.HTTPResponse;
import com.cool.request.utils.Base64Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Response implements HTTPResponse {
    private final HTTPResponseBody httpResponseBody;

    public Response(HTTPResponseBody httpResponseBody) {
        this.httpResponseBody = httpResponseBody;
    }

    @Override
    public byte[] getResponseBody() {
        return Base64Utils.decode(httpResponseBody.getBase64BodyData());
    }

    @Override
    public int getCode() {
        return httpResponseBody.getCode();
    }


    @Override
    public String getHeader(String key) {
        if (httpResponseBody.getHeader() == null) {
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
        if (httpResponseBody.getHeader() == null) {
            return new ArrayList<>();
        }
        return httpResponseBody.getHeader().stream().filter(header -> key.equalsIgnoreCase(header.getKey()))
                .map(Header::getValue).collect(Collectors.toList());
    }

    @Override
    public List<String> getHeaderKeys() {
        if (httpResponseBody.getHeader() == null) {
            return new ArrayList<>();
        }
        return httpResponseBody.getHeader().stream().map(Header::getKey).collect(Collectors.toList());
    }

}
