package com.cool.request.lib.springmvc.param;

import com.cool.request.lib.springmvc.ParameterAnnotationDescriptionUtils;
import com.cool.request.lib.springmvc.RequestParameterDescription;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicUrlParameterSpeculate {
    private final JacksonFieldAnnotationDescription jacksonFieldAnnotationDescription = new JacksonFieldAnnotationDescription();

    /**
     * 1.有@RequestParam注解，强制设置为参数，不管是不是基本数据类型
     * 2.没有@RequestParam注解,如果是基本数据类型，则设置为参数
     * 3.没有@RequestParam注解,不是基本数据类型,忽略
     */
    public List<RequestParameterDescription> get(PsiMethod method, boolean onlyHasRequestParam) {
        List<RequestParameterDescription> param = new ArrayList<>();

        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            //没有file文件，没有除了@RequestParam的其他Spring参数
            if (!ParamUtils.isMultipartFile(parameter) && !ParamUtils.hasSpringParamAnnotation(parameter, "RequestParam")) {
                if (ParamUtils.isHttpServlet(parameter)) {
                    continue;
                }
                //如果是基本数据类型
                if (ParamUtils.isBaseType(parameter.getType().getCanonicalText())) {
                    String paramName = parameter.getName();
                    if (requestParam != null) {
                        String requestParamValue = ParamUtils.getAnnotationStringValue(requestParam, "value");
                        if (!StringUtils.isEmpty(requestParamValue)) paramName = requestParamValue;

                    }
                    String description = ParameterAnnotationDescriptionUtils.getParameterDescription(parameter);
                    String type = ParamUtils.getParameterType(parameter);
                    if (onlyHasRequestParam && requestParam == null) continue;
                    param.add(new RequestParameterDescription(paramName, type, description));
                }

                String canonicalText = parameter.getType().getCanonicalText();
                if (ParamUtils.isUserObject(parameter.getType().getCanonicalText())) {
                    PsiClass psiClass = PsiUtils.findClassByName(method.getProject(), ModuleUtil.findModuleForPsiElement(method).getName(), canonicalText);
                    if (psiClass == null) continue;
                    for (PsiField field : ParamUtils.listCanApplyJsonField(psiClass)) {
                        String fieldName = jacksonFieldAnnotationDescription.getRelaName(field);
                        String description = ParameterAnnotationDescriptionUtils.getParameterDescription(parameter);
                        String type = ParamUtils.getParameterType(parameter);
                        param.add(new RequestParameterDescription(fieldName, type, description));
                    }
                }
            }
        }
        return param;
    }

}
