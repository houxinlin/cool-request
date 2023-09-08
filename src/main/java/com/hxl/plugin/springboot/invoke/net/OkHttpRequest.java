package com.hxl.plugin.springboot.invoke.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import okhttp3.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class OkHttpRequest {
    private OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OkHttpRequest() {
        okHttpClient = init(new OkHttpClient.Builder());
    }

    public <T> List<T> convertToList(String value, Class<T> tClass) {
        try {
            return objectMapper.readValue(value, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Call postFormUrlencoded(String url,Map<String,String> param,Headers headers){
        FormBody.Builder builder = new FormBody.Builder();
        param.forEach((key, value) -> builder.add(key,value));
        return okHttpClient.newCall(new Request.Builder()
                .post(builder.build())
                .headers(headers)
                .url(url)
                .build());
    }
    public Call postBody(String url, String body, String type, Headers headers) {
        return okHttpClient.newCall(new Request.Builder()
                .post(RequestBody.create(body, MediaType.get(type)))
                .headers(headers)
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

    public Call getBody(String url) {
        return okHttpClient.newCall(new Request.Builder()
                .get()
                .url(url)
                .build());
    }

    public abstract OkHttpClient init(OkHttpClient.Builder builder);

//    protected void addHeaderInterceptor(OkHttpClient.Builder builder, String name, List<String> ignore, Supplier<String> value) {
//        builder.addInterceptor(new HeaderInterceptor(name, value, ignore));
//    }
//
//    protected void addHeaderInterceptor(OkHttpClient.Builder builder, String name, String value) {
//        this.addHeaderInterceptor(builder, name, Collections.emptyList(), () -> value);
//    }

    public byte[] getByteBody(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().bytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check if the response is successful


//    private static class HeaderInterceptor implements Interceptor {
//        private final String headerName;
//        private final Supplier<String> headerValueSupplier;
//        private final List<String> ignore;
//
//        public HeaderInterceptor(String headerName, Supplier<String> headerValueSupplier, List<String> ignore) {
//            this.headerName = headerName;
//            this.headerValueSupplier = headerValueSupplier;
//            this.ignore = ignore;
//        }
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request originalRequest = chain.request();
//            String path = originalRequest.url().url().getPath();
//            if (ignore.indexOf(path) >= 0) return chain.proceed(originalRequest.newBuilder().build());
//            String value = headerValueSupplier.get();
//            if (StringUtils.isEmpty(value)) return chain.proceed(originalRequest.newBuilder().build());
//            Request modifiedRequest = originalRequest.newBuilder()
//                    .header(headerName, value)
//                    .build();
//            return chain.proceed(modifiedRequest);
//        }
//    }
}
