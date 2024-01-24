package com.cool.request.net;

import com.cool.request.Constant;
import com.cool.request.net.request.HttpRequestParamUtils;
import com.cool.request.net.request.StandardHttpRequestParam;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpRequestCallMethod extends BasicControllerRequestCallMethod {
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.HOURS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
    private final SimpleCallback simpleCallback;

    public HttpRequestCallMethod(StandardHttpRequestParam reflexHttpRequestParam, SimpleCallback simpleCallback) {
        super(reflexHttpRequestParam);
        this.simpleCallback = simpleCallback;

    }

    private void applyBodyIfNotGet(Request.Builder request) {
        if (!HttpMethod.GET.equals(getInvokeData().getMethod())) {
            String contentType = HttpRequestParamUtils.getContentType(getInvokeData(), MediaTypes.TEXT);
            if (!MediaTypes.MULTIPART_FORM_DATA.equals(contentType.toLowerCase())) {
                RequestBody requestBody = RequestBody.create(getInvokeData().getBody().contentConversion(), MediaType.parse(contentType));
                request.method(getInvokeData().getMethod().toString(), requestBody);
            }
        }
    }

    private void applyBodyIfForm(Request.Builder request) {
        String contentType = HttpRequestParamUtils.getContentType(getInvokeData(), MediaTypes.TEXT);
        if (!HttpMethod.GET.equals(getInvokeData().getMethod()) && MediaTypes.MULTIPART_FORM_DATA.equals(contentType.toLowerCase())) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            if (getInvokeData().getBody() instanceof FormBody) {
                for (FormDataInfo formDatum : ((FormBody) getInvokeData().getBody()).getData()) {
                    if (Constant.Identifier.FILE.equals(formDatum.getType())) {
                        File file = new File(formDatum.getValue());
                        builder.addFormDataPart(formDatum.getName(), file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")));
                    }
                    if (Constant.Identifier.TEXT.equals(formDatum.getType())) {
                        builder.addFormDataPart(formDatum.getName(), formDatum.getValue());
                    }
                }
                request.method(getInvokeData().getMethod().toString(), builder.build());
            }

        }
    }

    @Override
    public void invoke() {
        String fullUrl = HttpRequestParamUtils.getFullUrl(getInvokeData());
        Request.Builder request = new Request.Builder()
                .get()
                .url(fullUrl);
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
