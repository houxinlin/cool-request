/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HTTPResponseManager.java is part of Cool Request
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

package com.cool.request.components.http;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.components.http.net.HTTPHeader;
import com.cool.request.components.http.net.HTTPResponseBody;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

@Service
public final class HTTPResponseManager {
    private Project project;
    private List<HTTPResponseBodyConverter> httpResponseBodyFactories = new ArrayList<>();

    public HTTPResponseManager() {
        httpResponseBodyFactories.add(new OverflowHttpResponseBodyConverter());
    }

    public static HTTPResponseManager getInstance(Project project) {
        HTTPResponseManager service = project.getService(HTTPResponseManager.class);
        service.project = project;
        return service;
    }

    public byte[] bodyConverter(byte[] body, HTTPHeader header) {
        byte[] newHTTPResponseBody = body;
        for (HTTPResponseBodyConverter httpResponseBodyConverter : httpResponseBodyFactories) {
            newHTTPResponseBody = httpResponseBodyConverter.bodyConverter(newHTTPResponseBody, header);
        }
        return newHTTPResponseBody;
    }

    public void onHTTPResponse(HTTPResponseBody httpResponseBody) {

        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.HTTP_RESPONSE)
                .onResponseEvent(httpResponseBody.getId(), httpResponseBody, null);
    }
}
