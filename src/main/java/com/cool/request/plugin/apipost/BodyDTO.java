package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BodyDTO {
    @SerializedName("mode")
    private String mode;
    @SerializedName("parameter")
    private List<ParameterDTO> parameter;
    @SerializedName("raw")
    private String raw;
    @SerializedName("raw_para")
    private List<?> rawPara;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<ParameterDTO> getParameter() {
        return parameter;
    }

    public void setParameter(List<ParameterDTO> parameter) {
        this.parameter = parameter;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public List<?> getRawPara() {
        return rawPara;
    }

    public void setRawPara(List<?> rawPara) {
        this.rawPara = rawPara;
    }


    public static final class BodyDTOBuilder {
        private String mode;
        private List<ParameterDTO> parameter;
        private String raw;
        private List<?> rawPara;

        private BodyDTOBuilder() {
        }

        public static BodyDTOBuilder aBodyDTO() {
            return new BodyDTOBuilder();
        }

        public BodyDTOBuilder withMode(String mode) {
            this.mode = mode;
            return this;
        }

        public BodyDTOBuilder withParameter(List<ParameterDTO> parameter) {
            this.parameter = parameter;
            return this;
        }

        public BodyDTOBuilder withRaw(String raw) {
            this.raw = raw;
            return this;
        }

        public BodyDTOBuilder withRawPara(List<?> rawPara) {
            this.rawPara = rawPara;
            return this;
        }

        public BodyDTO build() {
            BodyDTO bodyDTO = new BodyDTO();
            bodyDTO.setMode(mode);
            bodyDTO.setParameter(parameter);
            bodyDTO.setRaw(raw);
            bodyDTO.setRawPara(rawPara);
            return bodyDTO;
        }
    }
}