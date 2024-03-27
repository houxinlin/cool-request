/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApipostExportResponse.java is part of Cool Request
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

public class ApipostExportResponse {

    @SerializedName("code")
    private Integer code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private DataDTO data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("socket_status")
        private Integer socketStatus;
        @SerializedName("repeat")
        private List<?> repeat;
        @SerializedName("success_apis")
        private List<String> successApis;
        @SerializedName("failed_apis")
        private List<?> failedApis;

        public Integer getSocketStatus() {
            return socketStatus;
        }

        public void setSocketStatus(Integer socketStatus) {
            this.socketStatus = socketStatus;
        }

        public List<?> getRepeat() {
            return repeat;
        }

        public void setRepeat(List<?> repeat) {
            this.repeat = repeat;
        }

        public List<String> getSuccessApis() {
            return successApis;
        }

        public void setSuccessApis(List<String> successApis) {
            this.successApis = successApis;
        }

        public List<?> getFailedApis() {
            return failedApis;
        }

        public void setFailedApis(List<?> failedApis) {
            this.failedApis = failedApis;
        }
    }
}
