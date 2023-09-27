package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.net.KeyValue;

import java.util.List;

public interface IRequestParamManager {
    public String getUrl();

    public HttpMethod getHttpMethod();

    public int getInvokeHttpMethod();

    public List<KeyValue> getHttpHeader();

    public List<KeyValue> getUrlParam();

    public List<FormDataInfo> getFormData();

    public List<KeyValue> getUrlencodedBody();

    public String getRequestBody();

    public String getRequestBodyType();

    public void setUrl(String url);

    public void setHttpMethod(HttpMethod method);

    public void setInvokeHttpMethod(int index);

    public void setHttpHeader(List<KeyValue> value);

    public void setUrlParam(List<KeyValue> value);

    public void setFormData(List<FormDataInfo> value);

    public void setUrlencodedBody(List<KeyValue> value);

    public void setRequestBody(String type ,String body);

    public void setRequestBodyType(String type);

    void setSendButtonEnabled(boolean b);
}
