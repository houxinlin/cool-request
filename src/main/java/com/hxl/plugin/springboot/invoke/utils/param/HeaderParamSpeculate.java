package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;

public class HeaderParamSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, RequestCache.RequestCacheBuilder requestCacheBuilder) {
        List<KeyValue> param = new ArrayList<>();
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestHeader");
            if (requestParam != null) {
                String value = ParamUtils.getPsiAnnotationValues(requestParam).get("value");
                if (StringUtils.isEmpty(value)) value = parameter.getName();
                param.add(new KeyValue(value, ""));
            }
        }
        requestCacheBuilder.withHeaders(param);
    }
}
