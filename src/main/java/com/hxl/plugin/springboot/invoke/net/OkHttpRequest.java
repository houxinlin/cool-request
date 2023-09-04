package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class OkHttpRequest {
    private OkHttpClient okHttpClient;

    public OkHttpRequest() {
        okHttpClient = init(new OkHttpClient.Builder());
    }

    public Call postBody(String url, String body, String type) {
        return okHttpClient.newCall(new Request.Builder()
                .post(RequestBody.create(body, MediaType.get(type)))
                .url(url)
                .build());
    }

    public Call getBody(String url, Headers headers) {
        return okHttpClient.newCall(new Request.Builder()
                .headers(headers)
                .get()
                .url(url)
                .build());
    }

    public abstract OkHttpClient init(OkHttpClient.Builder builder);

    protected void addHeaderInterceptor(OkHttpClient.Builder builder, String name, List<String> ignore, Supplier<String> value) {
        builder.addInterceptor(new HeaderInterceptor(name, value, ignore));
    }
    protected void addHeaderInterceptor(OkHttpClient.Builder builder, String name,  String value) {
       this.addHeaderInterceptor(builder, name, Collections.emptyList(), () -> value);
    }

    private static class HeaderInterceptor implements Interceptor {
        private final String headerName;
        private final Supplier<String> headerValueSupplier;
        private final List<String> ignore;

        public HeaderInterceptor(String headerName, Supplier<String> headerValueSupplier, List<String> ignore) {
            this.headerName = headerName;
            this.headerValueSupplier = headerValueSupplier;
            this.ignore = ignore;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String path = originalRequest.url().url().getPath();
            if (ignore.contains(path)) return chain.proceed(originalRequest.newBuilder().build());
            String value = headerValueSupplier.get();
            if (StringUtils.isEmpty(value)) return chain.proceed(originalRequest.newBuilder().build());
            Request modifiedRequest = originalRequest.newBuilder()
                    .header(headerName, value)
                    .build();
            return chain.proceed(modifiedRequest);
        }
    }
}
