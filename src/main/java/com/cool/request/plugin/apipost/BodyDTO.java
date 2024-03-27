/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BodyDTO.java is part of Cool Request
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