package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;


public class ResponseDTO {
    @SerializedName("success")
    private SuccessDTO success;

    public SuccessDTO getSuccess() {
        return success;
    }

    public void setSuccess(SuccessDTO success) {
        this.success = success;
    }


}