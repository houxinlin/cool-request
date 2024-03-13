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
