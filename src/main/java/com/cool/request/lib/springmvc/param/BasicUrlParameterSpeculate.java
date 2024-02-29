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
import java.util.Map;

public abstract class BasicUrlParameterSpeculate {
    /**
     * 把方法上所有参数，忽略数据类型，强制提取为url参数
     * 这是因为请求参数类型可能由用户自定义了参数解析器，可以吧字符转换为对象
     *
     * @param method 方法
     * @return 参数列表
     */
    public List<RequestParameterDescription> get(PsiMethod method) {
        return get(method, false);
    }

    /**
     * 1.有@RequestParam注解，强制设置为参数，不管是不是基本数据类型
     * 2.没有@RequestParam注解,如果是基本数据类型，则设置为参数
     * 3.没有@RequestParam注解,不是基本数据类型,忽略
     */
    public List<RequestParameterDescription> get(PsiMethod method, boolean onlyBaseType) {
        List<RequestParameterDescription> param = new ArrayList<>();

        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            if (!ParamUtils.isMultipartFile(parameter) && !ParamUtils.hasSpringParamAnnotation(parameter, "RequestParam")) {
                if (onlyBaseType && !ParamUtils.isBaseType(parameter.getType().getCanonicalText())) {
                    continue;
                }
                if (ParamUtils.isHttpServlet(parameter)) {
                    continue;
                }
                String canonicalText = parameter.getType().getCanonicalText();
                if (ParamUtils.isUserObject(canonicalText)) {
                    PsiClass psiClass = PsiUtils.findClassByName(method.getProject(), ModuleUtil.findModuleForPsiElement(method).getName(), canonicalText);
                    if (psiClass == null) continue;
                    if (!PsiUtils.hasExist(psiClass.getProject(), psiClass)) continue;
                    for (PsiField field : listCanApplyJsonField(psiClass)) {
                        String fieldName = field.getName();
                        String description = ParameterAnnotationDescriptionUtils.getParameterDescription(parameter);
                        String type = ParamUtils.getParameterType(parameter);
                        param.add(new RequestParameterDescription(fieldName, type, description));
                    }
                    continue;
                }

                Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
                String value = psiAnnotationValues.get("value");
                if (StringUtils.isEmpty(value)) {
                    value = parameter.getName();
                }
                String description = ParameterAnnotationDescriptionUtils.getParameterDescription(parameter);
                String type = ParamUtils.getParameterType(parameter);
                param.add(new RequestParameterDescription(value, type, description));
            }
        }
        return param;
    }

    protected List<PsiField> listCanApplyJsonField(PsiClass psiClass) {
        List<PsiField> result = new ArrayList<>();
        for (PsiField psiField : psiClass.getAllFields()) {
            if (isInstance(psiField)) result.add(psiField);
        }
        return result;
    }

    protected boolean isInstance(PsiField field) {
        return !field.hasModifierProperty(PsiModifier.STATIC);
    }
}
