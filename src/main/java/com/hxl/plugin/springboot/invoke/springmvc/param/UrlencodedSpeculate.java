package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.intellij.psi.PsiMethod;

public class UrlencodedSpeculate implements RequestParamSpeculate{
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(method.getParameterList().getParameters())){
//            List<KeyValue> param = new ArrayList<>(super.get(method));
//            requestCacheBuilder.withUrlencodedBody(param);
        }
    }
}
