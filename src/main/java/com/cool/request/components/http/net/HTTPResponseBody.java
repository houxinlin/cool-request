/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HTTPResponseBody.java is part of Cool Request
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

import java.util.ArrayList;
import java.util.List;

public class HTTPResponseBody {
    private List<Header> header = new ArrayList<>();
    private String baseBodyData = "";
    private String id = "";
    private int code = -1;
    private long size;

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public String getBase64BodyData() {
        return baseBodyData;
    }

    public void setBase64BodyData(String base64BodyData) {
        this.baseBodyData = base64BodyData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static final class InvokeResponseModelBuilder {
        private List<Header> header;
        private String data;
        private String id;

        private InvokeResponseModelBuilder() {
        }

        public static InvokeResponseModelBuilder anInvokeResponseModel() {
            return new InvokeResponseModelBuilder();
        }

        public InvokeResponseModelBuilder withHeader(List<Header> header) {
            this.header = header;
            return this;
        }

        public InvokeResponseModelBuilder withData(String data) {
            this.data = data;
            return this;
        }

        public InvokeResponseModelBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public HTTPResponseBody build() {
            HTTPResponseBody httpResponseBody = new HTTPResponseBody();
            httpResponseBody.setHeader(header);
            httpResponseBody.setBase64BodyData(data);
            httpResponseBody.setId(id);
            return httpResponseBody;
        }
    }
}
