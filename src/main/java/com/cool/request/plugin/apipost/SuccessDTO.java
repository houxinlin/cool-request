package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SuccessDTO {
    @SerializedName("raw")
    private String raw;
    @SerializedName("parameter")
    private List<?> parameter;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public List<?> getParameter() {
        return parameter;
    }

    public void setParameter(List<?> parameter) {
        this.parameter = parameter;
    }

}