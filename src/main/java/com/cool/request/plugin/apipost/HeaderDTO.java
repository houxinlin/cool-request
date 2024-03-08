package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class HeaderDTO {
    @SerializedName("parameter")
    private List<ParameterDTO> parameter;

    public List<ParameterDTO> getParameter() {
        return parameter;
    }

    public void setParameter(List<ParameterDTO> parameter) {
        this.parameter = parameter;
    }

    public static final class HeaderDTOBuilder {
        private List<ParameterDTO> parameter;

        private HeaderDTOBuilder() {
        }

        public static HeaderDTOBuilder aHeaderDTO() {
            return new HeaderDTOBuilder();
        }

        public HeaderDTOBuilder withParameter(List<ParameterDTO> parameter) {
            this.parameter = parameter;
            return this;
        }

        public HeaderDTO build() {
            HeaderDTO headerDTO = new HeaderDTO();
            headerDTO.setParameter(parameter);
            return headerDTO;
        }
    }
}