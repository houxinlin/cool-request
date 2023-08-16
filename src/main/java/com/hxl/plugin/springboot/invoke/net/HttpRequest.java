package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HttpRequest extends BaseRequest {
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private SimpleCallback simpleCallback;

    public HttpRequest(ControllerInvoke.ControllerRequestData controllerRequestData, SimpleCallback simpleCallback) {
        super(controllerRequestData);
        this.simpleCallback = simpleCallback;
    }

    private void applyBodyIfPost(Request.Builder request) {
        if (!"GET".equalsIgnoreCase(getInvokeData().getMethod())) {
            String contentType = getInvokeData().getContentType();
            RequestBody requestBody = RequestBody.create(getInvokeData().getBody(), MediaType.parse(contentType));
            request.method(getInvokeData().getMethod(), requestBody);
        }
    }

    @Override
    public void invoke() {
        Request.Builder request = new Request.Builder()
                .url(getInvokeData().getUrl());
        applyBodyIfPost(request);
        for (String headerKey : getInvokeData().getHeaders().keySet()) {
            request.addHeader(headerKey, getInvokeData().getHeaders().get(headerKey).toString());
        }
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                simpleCallback.onError(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                byte[] body = response.body() != null ? response.body().bytes() : new byte[0];
                simpleCallback.onResponse(getInvokeData().getId(), response.code(), response.headers().toMultimap(), body);
            }
        });
    }

    public interface SimpleCallback {
        public void onResponse(String requestId, int code, Map<String, List<String>> headers, byte[] response);

        public void onError(IOException e);
    }
}
