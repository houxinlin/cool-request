package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlParamSpeculate  extends BasicUrlParamSpeculate implements RequestParamSpeculate {

    @Override
    public void set(PsiMethod method, RequestCache.RequestCacheBuilder requestCacheBuilder) {
        if (ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;
        if (!ParamUtils.isGetRequest(method)) return;
        List<KeyValue> param = new ArrayList<>(super.get(method, requestCacheBuilder));
        requestCacheBuilder.withUrlParams(param);
    }
}
