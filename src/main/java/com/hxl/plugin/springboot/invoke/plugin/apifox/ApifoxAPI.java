package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.hxl.plugin.springboot.invoke.net.OkHttpRequest;
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import okhttp3.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ApifoxAPI extends OkHttpRequest {
    private static final String HOST = "https://api.apifox.cn";
    private static final String IMPORT_URL = "/api/v1/projects/{0}/import-data";
    private static final String GET_USER_INFO = "/api/v1/user?locale=zh-CN";

    @Override
    public OkHttpClient init(OkHttpClient.Builder builder) {
        addHeaderInterceptor(builder, "Authorization", List.of(GET_USER_INFO), () -> SettingPersistentState.getInstance().getState().apifoxCookie);
        addHeaderInterceptor(builder, "X-Apifox-Version", "2022-11-16");
        addHeaderInterceptor(builder,"User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
        addHeaderInterceptor(builder,"Host","api.apifox.cn");
        builder.followRedirects(true);
        builder.followSslRedirects(true);
        return builder.build();
    }

    public Call exportApi(Integer projectId, Map<String, Object> body) {
        String url = MessageFormat.format(IMPORT_URL, projectId.toString());
        return postBody(HOST.concat(url), ObjectMappingUtils.toJsonString(body), "application/json");
    }

    public String getUserInfo(String authorization) {
        Headers headers = new Headers.Builder()
                .add("Authorization", authorization)
                .build();
        Call body = getBody(HOST.concat(GET_USER_INFO), headers);
        try {
            Response execute = body.execute();
            ResponseBody responseBody = execute.body();
            String str = responseBody.string();
            if (responseBody!=null && execute.code()==200) return str;
        } catch (IOException ignored) {
        }
        return  null;
    }
}
