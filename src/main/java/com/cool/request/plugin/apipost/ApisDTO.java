/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApisDTO.java is part of Cool Request
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

public class ApisDTO {
    @SerializedName("name")
    private String name;
    @SerializedName("method")
    private String method;
    @SerializedName("request")
    private RequestDTO request;
    @SerializedName("response")
    private ResponseDTO response;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RequestDTO getRequest() {
        return request;
    }

    public void setRequest(RequestDTO request) {
        this.request = request;
    }

    public ResponseDTO getResponse() {
        return response;
    }

    public void setResponse(ResponseDTO response) {
        this.response = response;
    }


    public static final class ApisDTOBuilder {
        private String name;
        private String method;
        private RequestDTO request;
        private ResponseDTO response;

        private ApisDTOBuilder() {
        }

        public static ApisDTOBuilder anApisDTO() {
            return new ApisDTOBuilder();
        }

        public ApisDTOBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ApisDTOBuilder withMethod(String method) {
            this.method = method;
            return this;
        }

        public ApisDTOBuilder withRequest(RequestDTO request) {
            this.request = request;
            return this;
        }

        public ApisDTOBuilder withResponse(ResponseDTO response) {
            this.response = response;
            return this;
        }

        public ApisDTO build() {
            ApisDTO apisDTO = new ApisDTO();
            apisDTO.setName(name);
            apisDTO.setMethod(method);
            apisDTO.setRequest(request);
            apisDTO.setResponse(response);
            return apisDTO;
        }
    }
}
