/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * OkHttpRequest.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.components.http.net;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public abstract class OkHttpRequest {
    private final OkHttpClient okHttpClient;
    private final Gson gson = new Gson();

    public OkHttpRequest() {
        okHttpClient = init(new OkHttpClient.Builder());
    }


    public Call postFormUrlencoded(String url, Map<String, String> param, Headers headers) {
        FormBody.Builder builder = new FormBody.Builder();
        param.forEach(builder::add);
        return okHttpClient.newCall(new Request.Builder()
                .post(builder.build())
                .headers(headers)
                .url(url)
                .build());
    }

    public Call post(String url) {
        return okHttpClient.newCall(new Request.Builder()
                .url(url)
                .build());
    }

    public Call postBody(String url, String body, String type, Headers headers) {
        if (headers == null) {
            headers = new Headers.Builder().build();
        }
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
}
