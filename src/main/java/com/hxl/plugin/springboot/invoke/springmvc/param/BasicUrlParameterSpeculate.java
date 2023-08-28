package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.ParameterAnnotationDescriptionUtils;
import com.hxl.plugin.springboot.invoke.springmvc.RequestParameterDescription;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BasicUrlParameterSpeculate {
    public List<RequestParameterDescription> get(PsiMethod method) {
        List<RequestParameterDescription> param = new ArrayList<>();
        /**
         * 1.有@RequestParam注解，强制设置为参数，不管是不是基本数据类型
         * 2.没有@RequestParam注解,如果是基本数据类型，则设置为参数
         * 3.没有@RequestParam注解,不是基本数据类型,忽略
         */
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            if (requestParam != null && !ParamUtils.isMultipartFile(parameter)) {
                Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
                String value = psiAnnotationValues.get("value");
                if (StringUtils.isEmpty(value)) value = parameter.getName();
                String description = ParameterAnnotationDescriptionUtils.getParameterDescription(parameter);
                String type = ParamUtils.getParameterType(parameter);
                param.add(new RequestParameterDescription(value,type,description));
                continue;
            }
            if (!ParamUtils.hasSpringParamAnnotation(parameter) ) {
                String description = ParameterAnnotationDescriptionUtils.getParameterDescription(parameter);
                String type = ParamUtils.getParameterType(parameter);
                param.add(new RequestParameterDescription(parameter.getName(),type,description));
            }
        }
        return param;
    }
}
