package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormDataSpeculate  extends BasicUrlParamSpeculate  implements RequestParamSpeculate{
    @Override
    public void set(PsiMethod method, RequestCache.RequestCacheBuilder requestCacheBuilder) {
        if (!ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;

        List<FormDataInfo> param = new ArrayList<>();
        //??file
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            if (ParamUtils.isMultipartFile(parameter)) {
                Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
                String value = psiAnnotationValues.get("value");
                if (StringUtils.isEmpty(value)) value =parameter.getName();
                param.add(new FormDataInfo(value,"","file"));
            }
        }

        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            if (requestParam != null && !ParamUtils.isMultipartFile(parameter)) {
                Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
                String value = psiAnnotationValues.get("value");
                if (StringUtils.isEmpty(value)) value = parameter.getName();
                param.add(new FormDataInfo(value, "","text"));
                continue;
            }
            if (!ParamUtils.hasSpringParamAnnotation(parameter) &&
                    ParamUtils.isBaseType(parameter.getType().getCanonicalText())) {
                param.add(new FormDataInfo(parameter.getName(), "","text"));
            }
        }
        if (!param.isEmpty()) requestCacheBuilder.withRequestBodyType("form-data");
        requestCacheBuilder.withFormDataInfos(param);
    }
}
