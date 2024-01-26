package com.cool.request.lib.springmvc;

import com.cool.request.component.http.net.KeyValue;
import com.cool.request.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

public class FormUrlBody implements Body {
    private List<KeyValue> data;

    public FormUrlBody(List<KeyValue> tableMap) {
        this.data = tableMap;
        if (data == null) data = new ArrayList<>();
    }

    public List<KeyValue> getData() {
        return data;
    }

    @Override
    public byte[] contentConversion() {
        return UrlUtils.mapToUrlParams(data).getBytes();
    }
}
