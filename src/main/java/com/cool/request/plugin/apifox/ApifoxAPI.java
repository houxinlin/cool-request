package com.cool.request.plugin.apifox;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.components.http.net.OkHttpRequest;
import com.cool.request.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApifoxAPI extends OkHttpRequest {
    private static final String HOST = "https://api.apifox.cn";
    private static final String IMPORT_URL = "/api/v1/projects/{0}/import-data";
    private static final String GET_USER_INFO = "/api/v1/user?locale=zh-CN";
    private static final String GET_LIST_TEAM = "/api/v1/user-teams?locale=zh-CN";
    private static final String GET_LIST_PROJECT = "/api/v1/user-projects?locale=zh-CN";
    private static final String GET_LIST_FOLDERS = "/api/v1/api-detail-folders?locale=zh-CN";
    private static final String POST_CREATE_FOLDERS = "/api/v1/api-detail-folders?locale=zh-CN";
    private static final Headers DEFAULT_HEADER = new Headers.Builder()
            .add("X-Apifox-Version", "2022-11-16")
            .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
            .add("Host", "api.apifox.cn")
            .build();
    private final Gson gson = new Gson();


    public Map<String, Object> createNewFolderAndGet(int parentId, String name, int projectId) {
        Map<String, String> param = new HashMap<>();
        param.put("type", "http");
        param.put("parentId", String.valueOf(parentId));
        param.put("name", name);
        try {
            Response response = postFormUrlencoded(HOST.concat(POST_CREATE_FOLDERS), param, new Headers.Builder()
                    .addAll(getneratoBasicHeader())
                    .add("X-Project-Id", String.valueOf(projectId))
                    .build()).execute();
            if (response.code() == 200) {
                String body = response.body().string();
                Type mapType = new TypeToken<Map<String, Object>>() {
                }.getType();
                return gson.fromJson(body, mapType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @Override
    public OkHttpClient init(OkHttpClient.Builder builder) {
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(5, TimeUnit.SECONDS);
        builder.followRedirects(true);
        builder.followSslRedirects(true);
        return builder.build();
    }

    private Headers generatorExportHeader() {
        String openApiToken = SettingPersistentState.getInstance().getState().openApiToken;
        if (!openApiToken.startsWith("Bearer")) openApiToken = "Bearer " + openApiToken;
        return new Headers.Builder()
                .addAll(DEFAULT_HEADER)
                .add("Authorization", openApiToken)
                .build();
    }

    private Headers getneratoBasicHeader() {
        return new Headers.Builder()
                .addAll(DEFAULT_HEADER)
                .add("Authorization", SettingPersistentState.getInstance().getState().apiFoxAuthorization)
                .build();
    }

    public Call exportApi(Integer projectId, Map<String, Object> body) {
        String url = MessageFormat.format(IMPORT_URL, projectId.toString());
        return postBody(HOST.concat(url), GsonUtils.toJsonString(body), MediaTypes.APPLICATION_JSON, generatorExportHeader());
    }

    public Map<String, Object> exportApiAndGet(Integer projectId, Map<String, Object> body) {
        Call call = exportApi(projectId, body);
        try {
            String string = call.execute().body().string();
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            return gson.fromJson(string, mapType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ApifoxFolder listFolder(int projectId) {
        return doGet(GET_LIST_FOLDERS, ApifoxFolder.class,
                new Headers.Builder()
                        .addAll(getneratoBasicHeader())
                        .add("x-project-id", String.valueOf(projectId))
                        .build());
    }

    public ApifoxTeam listTeam() {
        return doGet(GET_LIST_TEAM, ApifoxTeam.class);
    }

    public ApifoxProject listProject() {
        return doGet(GET_LIST_PROJECT, ApifoxProject.class);
    }

    private <T> T doGet(String url, Class<T> tClass, Headers headers) {
        Call call = getBody(HOST.concat(url), headers);
        try {
            String body = call.execute().body().string();
            return GsonUtils.readValue(body, tClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> T doGet(String url, Class<T> tClass) {
        return doGet(url, tClass, getneratoBasicHeader());
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
            if (execute.code() == 200) return str;
        } catch (IOException ignored) {
        }
        return null;
    }
}
