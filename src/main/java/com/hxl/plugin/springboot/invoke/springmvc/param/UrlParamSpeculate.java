package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.springmvc.RequestParameterDescription;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public class UrlParamSpeculate extends BasicUrlParameterSpeculate implements RequestParamSpeculate {
    private boolean onlyBaseType = false; //默认只有基本数据类型
    private boolean onlyGetRequest = true;//默认只有GET请求

    public UrlParamSpeculate(boolean onlyBaseType, boolean onlyGetRequest) {
        this.onlyBaseType = onlyBaseType;
        this.onlyGetRequest = onlyGetRequest;
    }

    public UrlParamSpeculate() {
        this(false, true);
    }

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        //如果不是Get请求
        if (this.onlyGetRequest && ParamUtils.isNotGetRequest(method)) return;
        if (ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;
        List<RequestParameterDescription> requestParameterDescriptions = new ArrayList<>(super.get(method, onlyBaseType));
        httpRequestInfo.setUrlParams(requestParameterDescriptions);
    }
}
