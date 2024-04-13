/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * VersionInfoReport.java is part of Cool Request
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

import com.cool.request.utils.GsonUtils;
import com.intellij.openapi.application.ApplicationInfo;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VersionInfoReport extends OkHttpRequest {
    private static final String PING = "https://coolrequest.dev/api/ping";

    @Override
    public OkHttpClient init(OkHttpClient.Builder builder) {
        return builder.build();
    }

    public void report() {
        //上报使用信息
        try {
            ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
            Map<String, Object> body = new HashMap<>();
            body.put("osVersion", System.getProperty("os.version"));
            body.put("osName", System.getProperty("os.name"));
            body.put("idea", applicationInfo);
            postBody(PING, GsonUtils.toJsonString(body), MediaTypes.APPLICATION_JSON, new Headers.Builder().build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                }
            });
        } catch (Exception ignored) {
        }
    }
}
