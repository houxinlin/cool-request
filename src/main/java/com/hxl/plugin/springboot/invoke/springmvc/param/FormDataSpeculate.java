package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormDataSpeculate implements RequestParamSpeculate{
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        if (!ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;

        List<FormDataInfo> param = new ArrayList<>();
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            if (ParamUtils.isMultipartFile(parameter)) {
                Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
                String value = psiAnnotationValues.get("value");
                if (StringUtils.isEmpty(value)) value =parameter.getName();
                param.add(new FormDataInfo(value,"","file"));
            }
        }
//        for (PsiParameter parameter : method.getParameterList().getParameters()) {
//            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
//            if (requestParam != null && !ParamUtils.isMultipartFile(parameter)) {
//                Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
//                String value = psiAnnotationValues.get("value");
//                if (StringUtils.isEmpty(value)) value = parameter.getName();
//                param.add(new FormDataInfo(value, "","text"));
//                continue;
//            }
//            if (!ParamUtils.hasSpringParamAnnotation(parameter) &&
//                    ParamUtils.isBaseType(parameter.getType().getCanonicalText())) {
//                param.add(new FormDataInfo(parameter.getName(), "","text"));
//            }
//        }
        if (!param.isEmpty()) httpRequestInfo.setContentType("form-data");
        httpRequestInfo.setFormDataInfos(param);
//        requestCacheBuilder.withFormDataInfos(param);
    }
}
