package com.cool.request.view.main;

import com.cool.request.common.bean.BeanInvokeSetting;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.HttpMethod;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.view.tool.Provider;

import java.util.List;

public interface IRequestParamManager extends HTTPParamApply, Provider {
    public boolean isAvailable();

    public String getUrl();

    public BeanInvokeSetting getBeanInvokeSetting();

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

    public void setRequestBody(String type, String body);

    public void setRequestBodyType(String type);

    void setSendButtonEnabled(boolean b);

    public String getRequestScript();

    public String getResponseScript();

    public int getInvokeModelIndex();

    public Controller getCurrentController();

    public String getContentTypeFromHeader();

}
