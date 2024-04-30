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
import com.cool.request.script.FormURLEncodedBody;
import com.cool.request.script.HTTPResponse;
import com.cool.request.script.JSONBody;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.url.MultiValueMap;
import com.cool.request.utils.url.UriComponents;
import com.cool.request.utils.url.UriComponentsBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
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

    @Override
    public void setContentType(String type) {
        Iterator<Header> iterator = httpResponseBody.getHeader().iterator();
        while (iterator.hasNext()) {
            Header next = iterator.next();
            if ("content-type".equalsIgnoreCase(next.getKey())) {
                iterator.remove();
            }
        }
        httpResponseBody.getHeader().add(new Header("Content-Type", type));

    }

    @Override
    public FormURLEncodedBody getIfFormURLEncodedBody() {
        return new FormURLEncodedBody() {
            @Override
            public List<String> getValue(String key) {
                UriComponents uriComponents = UriComponentsBuilder.newInstance().query(new String(getResponseBody())).build();
                MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
                if (queryParams != null) return queryParams.get(key);
                return null;
            }

            @Override
            public String getOneValue(String key) {
                List<String> value = getValue(key);
                if (value != null && !value.isEmpty()) return value.get(0);
                return null;
            }
        };
    }

    @Override
    public JSONBody getIfJSONBody() {
        return key -> {
            TypeFactory typeFactory = TypeFactory.defaultInstance();
            MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);
            try {
                return ((Map<String,Object>)(new ObjectMapper().readValue(((new String(getResponseBody()))), mapType))).get(key);
            } catch (Exception ignored) {
            }
            return null;
        };
    }

    @Override
    public void addHeader(String key, String value) {
        httpResponseBody.getHeader().add(new Header(key, value));
    }
}
