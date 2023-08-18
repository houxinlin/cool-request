package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.net.KeyValue;

import java.util.List;

public interface IRequestParam {
    public String getUrl();

    public HttpMethod getHttpMethod();

    public String getInvokeHttpMethod();

    public List<KeyValue> getHttpHeader();

    public List<KeyValue> getUrlParam();

    public List<FormDataInfo> getFormData();

    public List<KeyValue> getUrlencodedBody();

    public String getRequestBody();


    public void setUrl();

    public void setHttpMethod();

    public void setInvokeHttpMethod();

    public void setHttpHeader(List<KeyValue> value);

    public void setUrlParam(List<KeyValue> value);

    public void setFormData(List<FormDataInfo> value);

    public void setUrlencodedBody(List<FormDataInfo> value);

    public void setRequestBody(String body);
}
