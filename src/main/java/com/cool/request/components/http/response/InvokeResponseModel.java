/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * InvokeResponseModel.java is part of Cool Request
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

package com.cool.request.components.http.response;

import com.cool.request.components.http.Header;

import java.io.Serializable;
import java.util.List;

public class InvokeResponseModel implements Serializable {
    private static final long serialVersionUID = 1000000;
    private List<Header> header;
    private String baseBodyData;
    private String id;
    private int code = -1;
    private Object attachData;

    public Object getAttachData() {
        return attachData;
    }

    public void setAttachData(Object attachData) {
        this.attachData = attachData;
    }

    public String headerToString() {
        StringBuilder headerStringBuffer = new StringBuilder();
        for (Header header : getHeader()) {
            headerStringBuffer.append(header.getKey()).append(": ").append(header.getValue());
            headerStringBuffer.append("\n");
        }
        return headerStringBuffer.toString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Header> getHeader() {
        return header;
    }

    public void setHeader(List<Header> header) {
        this.header = header;
    }

    public String getBaseBodyData() {
        return baseBodyData;
    }

    public void setBaseBodyData(String baseBodyData) {
        this.baseBodyData = baseBodyData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
