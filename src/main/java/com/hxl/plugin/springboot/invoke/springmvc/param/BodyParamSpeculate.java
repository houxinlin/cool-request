package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.net.MediaTypes;
import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.springmvc.JSONObjectBody;
import com.hxl.plugin.springboot.invoke.springmvc.StringBody;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BodyParamSpeculate implements RequestParamSpeculate {

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        Map<String, Object> result = new HashMap<>();
        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(parameters)) {
            if (!ParamUtils.hasUserObject(method))return;

            if (ParamUtils.hasSpringMvcRequestParamAnnotation(method) || ParamUtils.hasBaseType(method)) {
                new UrlParamSpeculate(true,false).set(method, httpRequestInfo);
            }
            //匹配到地一个用户数据类时候返回
            for (PsiParameter parameter : parameters) {
//                PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestBody");
                String canonicalText = parameter.getType().getCanonicalText();
                if (ParamUtils.isUserObject(canonicalText)) {
                    PsiClass psiClass = PsiUtils.findClassByName(method.getProject(), parameter.getType().getCanonicalText());
                    if (psiClass == null) return;
                    for (PsiField field : psiClass.getAllFields()) {
                        result.put(field.getName(), getTargetValue(field));
                    }
                    httpRequestInfo.setRequestBody(new JSONObjectBody(result));
                    httpRequestInfo.setContentType(MediaTypes.APPLICATION_JSON);
                    break;
                }
            }
            //如果是string类型
            if (parameters.length == 1 && ParamUtils.isString(parameters[0].getType().getCanonicalText())) {
                PsiAnnotation requestBody = parameters[0].getAnnotation("org.springframework.web.bind.annotation.RequestBody");
                if (requestBody != null) {
                    httpRequestInfo.setRequestBody(new StringBody(""));
                    httpRequestInfo.setContentType(MediaTypes.TEXT);
                }
            }
        }
    }

    private Object getTargetValue(PsiField itemField) {
        String canonicalText = itemField.getType().getCanonicalText();
        if (ParamUtils.isChar(canonicalText)) return '\0';
        if (ParamUtils.isArray(canonicalText)) return Collections.EMPTY_LIST;
        if (ParamUtils.isString(canonicalText)) return "";
        if (ParamUtils.isNumber(canonicalText)) return 0;
        if (ParamUtils.isFloat(canonicalText)) return 0.0f;
        if (ParamUtils.isBoolean(canonicalText)) return false;
        if (ParamUtils.isMap(itemField)) return Collections.EMPTY_MAP;

        if (!ParamUtils.isJdkClass(itemField.getType().getCanonicalText())) {
            PsiClass psiClass = PsiUtils.findClassByName(itemField.getProject(), itemField.getType().getCanonicalText());
            if (psiClass == null) return "";
            Map<String, Object> result = new HashMap<>();
            for (PsiField field : psiClass.getAllFields()) {
                if("byte[]".equals(field.getType().getCanonicalText())){
                    result.put(field.getName(),new ArrayList<>());
                    continue;
                }
                if (ParamUtils.isBaseType(field.getType().getCanonicalText())) {
                    result.put(field.getName(), getTargetValue(field));
                }
            }
            return result;
        }
        return "";
    }
}
