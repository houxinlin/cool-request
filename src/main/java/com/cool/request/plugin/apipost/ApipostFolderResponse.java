package com.cool.request.plugin.apipost;

import java.util.List;

public class ApipostFolderResponse {
    private Integer code;
    private String msg;
    private List<ApipostFolder> data;

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

    public List<ApipostFolder> getData() {
        return data;
    }

    public void setData(List<ApipostFolder> data) {
        this.data = data;
    }
}
