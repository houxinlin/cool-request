/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApipostAPI.java is part of Cool Request
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

package com.cool.request.plugin.apipost;

import com.cool.request.common.state.ThirdPartyPersistent;
import com.cool.request.components.http.net.OkHttpRequest;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.StringUtils;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApipostAPI extends OkHttpRequest {
    private static final String GET_PROJECT_API = "open_ide/apis_project/team_project";
    private static final String GET_FOLDER_API = "open_ide/apis_project/project_folder";
    private static final String CREATE_FOLDER_API = "open_ide/apis_project/create_folder";
    private static final String EXPORT_API = "open_ide/convent";
    private static final Headers DEFAULT_HEADER = new Headers.Builder()
            .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
            .build();
    @SuppressWarnings("all")
    @Override
    public OkHttpClient init(OkHttpClient.Builder builder) {
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(3, TimeUnit.MINUTES);
        builder.followRedirects(true);
        builder.followSslRedirects(true);
        return builder.build();
    }

    private String getFullUrl(String subPath) {
        return StringUtils.joinUrlPath(ThirdPartyPersistent.getInstance().apipostHost, subPath);
    }

    private Headers generatorBasicHeader() {
        return new Headers.Builder()
                .addAll(DEFAULT_HEADER)
                .addUnsafeNonAscii("api-token", ThirdPartyPersistent.getInstance().apipostToken)
                .build();
    }

    public ApipostProjectResponse listProject() throws IOException {
        return doGet(getFullUrl(GET_PROJECT_API), ApipostProjectResponse.class, generatorBasicHeader());
    }

    public ApipostFolderResponse listFolder(String projectId) throws IOException {
        return doGet(getFullUrl(GET_FOLDER_API).concat("?project_id=" + projectId), ApipostFolderResponse.class, generatorBasicHeader());
    }

    private <T> T doGet(String url, Class<T> tClass, Headers headers) throws IOException {
        Call call = getBody(url, headers);
        String body = getResponse(call.execute().body());
        return GsonUtils.readValue(body, tClass);
    }


    public ApipostCreateFolderResponse createFolder(ApipostCreateFolderRequestBody apipostCreateFolderRequestBody) throws IOException {
        Call call = postBody(getFullUrl(CREATE_FOLDER_API),
                GsonUtils.toJsonString(apipostCreateFolderRequestBody), "application/json", generatorBasicHeader());
        String body = getResponse(call.execute().body());
        return GsonUtils.readValue(body, ApipostCreateFolderResponse.class);
    }

    public ApipostExportResponse export(ApipostControllerRequestBody apipostControllerRequestBody) throws IOException {
        Call call = postBody(getFullUrl(EXPORT_API),
                GsonUtils.toJsonString(apipostControllerRequestBody), "application/json", generatorBasicHeader());
        String body = getResponse(call.execute().body());
        return GsonUtils.readValue(body, ApipostExportResponse.class);
    }

    private String getResponse(ResponseBody body) throws IOException {
        if (body == null) throw new IOException("Read Error");
        return body.string();
    }

    public boolean checkToken(String host, String token) throws IOException {
        Headers headers = new Headers.Builder()
                .addAll(DEFAULT_HEADER)
                .addUnsafeNonAscii("api-token", token)
                .build();
        ApipostProjectResponse apipostProjectResponse = doGet(StringUtils.joinUrlPath(host, GET_PROJECT_API),
                ApipostProjectResponse.class, headers);
        return (apipostProjectResponse != null && apipostProjectResponse.getCode().equals(10000));
    }
}
