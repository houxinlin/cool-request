package com.cool.request.lib.springmvc.param;

import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.ParameterAnnotationDescriptionUtils;
import com.cool.request.components.http.RequestParameterDescription;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;

public class HeaderParamSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        List<RequestParameterDescription> headerParam = new ArrayList<>();
        for (PsiParameter parameter : method.getParameterList().getParameters()) {

            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestHeader");
            if (requestParam != null) {
                String value = ParamUtils.getPsiAnnotationValues(requestParam).get("value");
                if (StringUtils.isEmpty(value)) value = parameter.getName();
                String description = ParameterAnnotationDescriptionUtils.getParameterDescription(parameter);
                String type = ParamUtils.getParameterType(parameter);
                headerParam.add(new RequestParameterDescription(value, type, description));
            }
            httpRequestInfo.setHeaders(headerParam);
        }
    }
}
