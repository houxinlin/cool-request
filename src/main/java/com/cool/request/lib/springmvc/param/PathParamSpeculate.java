package com.cool.request.lib.springmvc.param;

import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.components.http.RequestParameterDescription;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;

public class PathParamSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        List<RequestParameterDescription> param = new ArrayList<>();

        for (PsiParameter parameter : listCanSpeculateParam(method)) {
            PsiAnnotation annotation = AnnotationUtil.findAnnotation(parameter, "org.springframework.web.bind.annotation.PathVariable");
            if (annotation == null) continue;
            String value = ParamUtils.getAnnotationStringValue(annotation, "value");
            if (!StringUtils.isEmpty(value)) {
                param.add(new RequestParameterDescription(value, "text", ""));
            } else {
                param.add(new RequestParameterDescription(parameter.getName(), "text", ""));
            }
        }
        httpRequestInfo.setPathParams(param);
    }
}
