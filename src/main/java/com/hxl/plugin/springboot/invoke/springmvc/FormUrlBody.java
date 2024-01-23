package com.hxl.plugin.springboot.invoke.springmvc;

import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.utils.UrlUtils;

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
