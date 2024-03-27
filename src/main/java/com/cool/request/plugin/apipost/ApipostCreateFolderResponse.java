/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApipostCreateFolderResponse.java is part of Cool Request
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

public class ApipostCreateFolderResponse {

    @SerializedName("code")
    private Integer code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private List<DataDTO> data;

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

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("folder")
        private String folder;
        @SerializedName("target_id")
        private String targetId;

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }
}
