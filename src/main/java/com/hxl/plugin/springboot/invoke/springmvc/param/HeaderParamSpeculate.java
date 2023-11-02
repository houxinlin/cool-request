package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.springmvc.ParameterAnnotationDescriptionUtils;
import com.hxl.plugin.springboot.invoke.springmvc.RequestParameterDescription;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
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
                headerParam.add(new RequestParameterDescription(value,type,description));
            }
        httpRequestInfo.setHeaders(headerParam);
        }
    }
}
