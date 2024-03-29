/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * Response.java is part of Cool Request
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

package com.cool.request.components.http.script;

import com.cool.request.components.http.Header;
import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.script.HTTPResponse;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Response implements HTTPResponse {
    private final HTTPResponseBody httpResponseBody;

    public Response(HTTPResponseBody httpResponseBody) {
        this.httpResponseBody = httpResponseBody;
    }

    @Override
    public byte[] getResponseBody() {
        return Base64Utils.decode(httpResponseBody.getBase64BodyData());
    }

    @Override
    public int getCode() {
        return httpResponseBody.getCode();
    }


    @Override
    public String getHeader(String key) {
        if (httpResponseBody.getHeader() == null) {
            return null;
        }
        List<String> headers = getHeaders(key);
        if (!headers.isEmpty()) {
            return headers.get(0);
        }
        return null;
    }

    @Override
    public List<String> getHeaders(String key) {
        if (httpResponseBody.getHeader() == null) {
            return new ArrayList<>();
        }
        return httpResponseBody.getHeader().stream().filter(header -> key.equalsIgnoreCase(header.getKey()))
                .map(Header::getValue).collect(Collectors.toList());
    }

    @Override
    public void setResponseBody(byte[] body) {
        if (body == null) throw new IllegalArgumentException("body is null");
        httpResponseBody.setBase64BodyData(Base64Utils.encodeToString(body));
    }

    @Override
    public void setResponseBody(String body) {
        if (StringUtils.isEmpty(body)) throw new IllegalArgumentException("body is null");
        httpResponseBody.setBase64BodyData(Base64Utils.encodeToString(body.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public List<String> getHeaderKeys() {
        if (httpResponseBody.getHeader() == null) {
            return new ArrayList<>();
        }
        return httpResponseBody.getHeader().stream().map(Header::getKey).collect(Collectors.toList());
    }

}
