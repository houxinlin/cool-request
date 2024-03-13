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
