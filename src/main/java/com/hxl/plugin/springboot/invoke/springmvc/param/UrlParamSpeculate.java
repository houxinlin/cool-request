package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.springmvc.RequestParameterDescription;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public class UrlParamSpeculate  extends BasicUrlParameterSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        //如果不是Get请求
        if (ParamUtils.isNotGetRequest(method) && ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;
        List<RequestParameterDescription> requestParameterDescriptions = new ArrayList<>(super.get(method));
        httpRequestInfo.setUrlParams(requestParameterDescriptions);
    }
}
