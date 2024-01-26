package com.cool.request.component.http.net;

import com.cool.request.utils.ObjectMappingUtils;
import com.intellij.openapi.application.ApplicationInfo;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VersionInfoReport extends OkHttpRequest {
    private static final String PING = "https://plugin.houxinlin.com/api/ping";

    @Override
    public OkHttpClient init(OkHttpClient.Builder builder) {
        return builder.build();
    }

    public void report() {
        //上报使用信息
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        Map<String, Object> body = new HashMap<>();
        body.put("osVersion", System.getProperty("os.version"));
        body.put("osName", System.getProperty("os.name"));
        body.put("idea", applicationInfo);
        postBody(PING, ObjectMappingUtils.toJsonString(body), MediaTypes.APPLICATION_JSON, new Headers.Builder().build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }
}
