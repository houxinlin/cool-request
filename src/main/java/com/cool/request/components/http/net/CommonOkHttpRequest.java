package com.cool.request.components.http.net;

import okhttp3.OkHttpClient;

public class CommonOkHttpRequest  extends OkHttpRequest{
    @Override
    public OkHttpClient init(OkHttpClient.Builder builder) {
        return builder.build();
    }

}
