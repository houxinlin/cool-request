package com.hxl.plugin.springboot.invoke.net;

import okhttp3.*;

import java.io.IOException;

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

    public abstract OkHttpClient init(OkHttpClient.Builder builder);

    protected void addHeaderInterceptor(OkHttpClient.Builder builder, String name, String value) {
        builder.addInterceptor(new HeaderInterceptor(name, value));
    }

    private static class HeaderInterceptor implements Interceptor {
        private final String headerName;
        private final String headerValue;

        public HeaderInterceptor(String headerName, String headerValue) {
            this.headerName = headerName;
            this.headerValue = headerValue;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request modifiedRequest = originalRequest.newBuilder()
                    .header(headerName, headerValue)
                    .build();
            return chain.proceed(modifiedRequest);
        }
    }
}
