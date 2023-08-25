package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BasicUrlParamSpeculate  {
    public List<KeyValue> get(PsiMethod method, RequestCache.RequestCacheBuilder requestCacheBuilder) {
        List<KeyValue> param = new ArrayList<>();
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            if (requestParam != null && !ParamUtils.isMultipartFile(parameter)) {
                Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
                String value = psiAnnotationValues.get("value");
                if (StringUtils.isEmpty(value)) value = parameter.getName();
                param.add(new KeyValue(value, ""));
                continue;
            }
            //????????????
            if (!ParamUtils.hasSpringParamAnnotation(parameter) &&
                    ParamUtils.isBaseType(parameter.getType().getCanonicalText())) {
                param.add(new KeyValue(parameter.getName(), ""));
            }
        }
        return param;
    }
}
