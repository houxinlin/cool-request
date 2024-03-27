/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestDTO.java is part of Cool Request
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

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestDTO {
    @SerializedName("url")
    private String url;
    @SerializedName("path")
    private String path;
    @SerializedName("pre_url")
    private String preUrl;
    @SerializedName("module")
    private String module;
    @SerializedName("folder")
    private String folder;
    @SerializedName("body")
    private BodyDTO body;
    @SerializedName("query")
    private QueryDTO query;
    @SerializedName("resful")
    private RequestDTO.ResfulDTO resful;
    @SerializedName("header")
    private HeaderDTO header;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPreUrl() {
        return preUrl;
    }

    public void setPreUrl(String preUrl) {
        this.preUrl = preUrl;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public BodyDTO getBody() {
        return body;
    }

    public void setBody(BodyDTO body) {
        this.body = body;
    }

    public QueryDTO getQuery() {
        return query;
    }

    public void setQuery(QueryDTO query) {
        this.query = query;
    }

    public RequestDTO.ResfulDTO getResful() {
        return resful;
    }

    public void setResful(RequestDTO.ResfulDTO resful) {
        this.resful = resful;
    }

    public HeaderDTO getHeader() {
        return header;
    }

    public void setHeader(HeaderDTO header) {
        this.header = header;
    }


    public static class ResfulDTO {
        @SerializedName("parameter")
        private List<?> parameter;

        public List<?> getParameter() {
            return parameter;
        }

        public void setParameter(List<?> parameter) {
            this.parameter = parameter;
        }
    }


    public static final class RequestDTOBuilder {
        private String url;
        private String path;
        private String preUrl;
        private String module;
        private String folder;
        private BodyDTO body;
        private QueryDTO query;
        private ResfulDTO resful;
        private HeaderDTO header;

        private RequestDTOBuilder() {
        }

        public static RequestDTOBuilder aRequestDTO() {
            return new RequestDTOBuilder();
        }

        public RequestDTOBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public RequestDTOBuilder withPath(String path) {
            this.path = path;
            return this;
        }

        public RequestDTOBuilder withPreUrl(String preUrl) {
            this.preUrl = preUrl;
            return this;
        }

        public RequestDTOBuilder withModule(String module) {
            this.module = module;
            return this;
        }

        public RequestDTOBuilder withFolder(String folder) {
            this.folder = folder;
            return this;
        }

        public RequestDTOBuilder withBody(BodyDTO body) {
            this.body = body;
            return this;
        }

        public RequestDTOBuilder withQuery(QueryDTO query) {
            this.query = query;
            return this;
        }

        public RequestDTOBuilder withResful(ResfulDTO resful) {
            this.resful = resful;
            return this;
        }

        public RequestDTOBuilder withHeader(HeaderDTO header) {
            this.header = header;
            return this;
        }

        public RequestDTO build() {
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setUrl(url);
            requestDTO.setPath(path);
            requestDTO.setPreUrl(preUrl);
            requestDTO.setModule(module);
            requestDTO.setFolder(folder);
            requestDTO.setBody(body);
            requestDTO.setQuery(query);
            requestDTO.setResful(resful);
            requestDTO.setHeader(header);
            return requestDTO;
        }
    }
}