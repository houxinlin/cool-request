package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.springmvc.RequestParameterDescription;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class UrlencodedSpeculate  extends BasicUrlParameterSpeculate implements RequestParamSpeculate{
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(method.getParameterList().getParameters())){
            List<RequestParameterDescription> param = new ArrayList<>(super.get(method));
            httpRequestInfo.setUrlencodedBody(param);
        }
    }
}
