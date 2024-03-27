/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HTTPHeader.java is part of Cool Request
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

import com.cool.request.components.http.Header;
import com.cool.request.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class HTTPHeader {
    public static final String CONTENT_TYPE = "Content-Type";
    private List<Header> headers;

    public HTTPHeader(List<Header> headers) {
        this.headers = headers;
        if (this.headers == null) this.headers = new ArrayList<>();
    }

    public String headerToString() {
        StringBuilder headerStringBuffer = new StringBuilder();
        for (Header header : headers) {
            headerStringBuffer.append(header.getKey()).append(": ").append(header.getValue());
            headerStringBuffer.append("\n");
        }
        return headerStringBuffer.toString();
    }

    public String getContentType(String defaultValue) {
        String contentType = defaultValue;
        for (Header header : headers) {
            if ("content-type".equalsIgnoreCase(header.getKey()) && !StringUtils.isEmpty(header.getValue())) {
                contentType = header.getValue();
            }
        }
        return contentType;
    }

    public void setHeader(String key, String value) {
        if (headers == null) return;
        headers.removeIf(header -> StringUtils.isEqualsIgnoreCase(header.getKey(), key));
        headers.add(new Header(key, value));
    }
}
