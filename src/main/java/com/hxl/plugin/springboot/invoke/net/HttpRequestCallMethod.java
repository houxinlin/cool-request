package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;
import com.hxl.plugin.springboot.invoke.utils.exception.RequestParamException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class HttpRequestCallMethod extends BasicControllerRequestCallMethod {
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.HOURS)
            .connectTimeout(5,TimeUnit.SECONDS)
            .build();
    private final SimpleCallback simpleCallback;

    public HttpRequestCallMethod(ControllerRequestData controllerRequestData, SimpleCallback simpleCallback) {
        super(controllerRequestData);
        this.simpleCallback = simpleCallback;

    }

    private void applyBodyIfNotGet(Request.Builder request) {
        if (!"GET".equalsIgnoreCase(getInvokeData().getMethod())) {
            String contentType = getInvokeData().getContentType();
            if (getInvokeData().isBinaryBody()) {
                if (Files.exists(Paths.get(getInvokeData().getBody())))
                    try {
                        RequestBody requestBody = RequestBody.create(Files.readAllBytes(Paths.get(getInvokeData().getBody())), MediaType.parse(contentType));
                        request.method(getInvokeData().getMethod(), requestBody);
                        return;
                    } catch (Exception ignored) {
                    }
                throw new RequestParamException(getInvokeData().getBody() + " Not Exist");
            } else {
                RequestBody requestBody = RequestBody.create(getInvokeData().getBody(), MediaType.parse(contentType));
                request.method(getInvokeData().getMethod(), requestBody);
            }
        }
    }

    private void applyBodyIfForm(Request.Builder request) {
        if (!"GET".equalsIgnoreCase(getInvokeData().getMethod()) && MediaTypes.MULTIPART_FORM_DATA.equals(getInvokeData().getContentType())) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            for (FormDataInfo formDatum : getInvokeData().getFormData()) {
                if (Constant.Identifier.FILE.equals(formDatum.getType())) {
                    File file = new File(formDatum.getValue());
                    builder.addFormDataPart(formDatum.getName(), file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")));
                }
                if (Constant.Identifier.TEXT.equals(formDatum.getType())) {
                    builder.addFormDataPart(formDatum.getName(), formDatum.getValue());
                }
            }
            request.method(getInvokeData().getMethod(), builder.build());
        }
    }

    @Override
    public void invoke() {
        Request.Builder request = new Request.Builder()
                .get()
                .url(getInvokeData().getUrl());
        applyBodyIfNotGet(request);
        applyBodyIfForm(request);
        Headers.Builder builder = new Headers.Builder();

        for (KeyValue header : getInvokeData().getHeaders()) {
            builder.addUnsafeNonAscii(header.getKey(), header.getValue());
        }
        request.headers(builder.build());

        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                simpleCallback.onError(getInvokeData().getId(), e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                simpleCallback.onResponse(getInvokeData().getId(), response.code(), response);
            }
        });
    }


    public interface SimpleCallback {
        public void onResponse(String requestId, int code, Response response);

        public void onError(String requestId, IOException e);
    }
}
