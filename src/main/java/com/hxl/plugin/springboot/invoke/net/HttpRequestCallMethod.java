package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class HttpRequestCallMethod extends BasicRequestCallMethod {
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final SimpleCallback simpleCallback;

    public HttpRequestCallMethod(ControllerInvoke.ControllerRequestData controllerRequestData, SimpleCallback simpleCallback) {
        super(controllerRequestData);
        this.simpleCallback = simpleCallback;
    }
    private void applyBodyIfNotGet(Request.Builder request) {
        if (!"GET".equalsIgnoreCase(getInvokeData().getMethod())) {
            String contentType = getInvokeData().getContentType();
            RequestBody requestBody = RequestBody.create(getInvokeData().getBody(), MediaType.parse(contentType));
            request.method(getInvokeData().getMethod(), requestBody);
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
        for (KeyValue header : getInvokeData().getHeaders()) {
            request.addHeader(header.getKey(), header.getValue());
        }
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                simpleCallback.onError(getInvokeData().getId(), e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                simpleCallback.onResponse(getInvokeData().getId(), response.code(), response);
            }
        });
    }


    public interface SimpleCallback {
        public void onResponse(String requestId, int code, Response response);

        public void onError(String requestId, IOException e);
    }
}
