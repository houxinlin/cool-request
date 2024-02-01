package com.cool.request.lib.springmvc;

import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.MediaTypes;

import java.util.ArrayList;
import java.util.List;

public class FormBody implements Body {
    private List<FormDataInfo> data;

    public FormBody(List<FormDataInfo> formData) {
        this.data = formData;
        if (data == null) data = new ArrayList<>();
    }

    public List<FormDataInfo> getData() {
        return data;
    }

    @Override
    public byte[] contentConversion() {
        return new byte[0];
    }

    @Override
    public String getMediaType() {
        return MediaTypes.MULTIPART_FORM_DATA;
    }
}
