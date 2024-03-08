package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class QueryDTO {
    @SerializedName("parameter")
    private List<ParameterDTO> parameter;

    public List<ParameterDTO> getParameter() {
        return parameter;
    }

    public void setParameter(List<ParameterDTO> parameter) {
        this.parameter = parameter;
    }

    public static final class QueryDTOBuilder {
        private List<ParameterDTO> parameter;

        private QueryDTOBuilder() {
        }

        public static QueryDTOBuilder aQueryDTO() {
            return new QueryDTOBuilder();
        }

        public QueryDTOBuilder withParameter(List<ParameterDTO> parameter) {
            this.parameter = parameter;
            return this;
        }

        public QueryDTO build() {
            QueryDTO queryDTO = new QueryDTO();
            queryDTO.setParameter(parameter);
            return queryDTO;
        }
    }
}