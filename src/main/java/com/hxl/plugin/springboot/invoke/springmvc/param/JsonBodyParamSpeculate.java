package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.intellij.psi.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonBodyParamSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        Map<String, Object> result = new HashMap<>();
        if (!ParamUtils.isGetRequest(method)) {
            for (PsiParameter parameter : method.getParameterList().getParameters()) {
                PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestBody");
                //??RequestBody???????????????????
                String canonicalText = parameter.getType().getCanonicalText();
                if (requestParam != null &&
                        !ParamUtils.isArray(canonicalText) &&
                        !ParamUtils.isBaseType(canonicalText) &&
                        !ParamUtils.isJdkClass(canonicalText)) {
                    PsiClass psiClass = PsiUtils.findClassByName(method.getProject(), parameter.getType().getCanonicalText());
                    if (psiClass == null) return;
                    for (PsiField field : psiClass.getAllFields()) {
                        result.put(field.getName(), getTargetValue(field));
                    }
//                    requestCacheBuilder.withRequestBodyType("application/json");
                }
            }
//            requestCacheBuilder.withRequestBody(new Gson().toJson(result));
        }
    }

    private Object getTargetValue(PsiField itemField){
        String canonicalText = itemField.getType().getCanonicalText();
        if (ParamUtils.isChar(canonicalText)) return '\0';
        if (ParamUtils.isArray(canonicalText)) return Collections.EMPTY_LIST;
        if (ParamUtils.isString(canonicalText)) return "";
        if (ParamUtils.isNumber(canonicalText)) return 0;
        if (ParamUtils.isFloat(canonicalText)) return 0.0f;
        if (ParamUtils.isBoolean(canonicalText)) return false;
        if (ParamUtils.isMap(itemField)) return Collections.EMPTY_MAP;

        if (!ParamUtils.isJdkClass(itemField.getType().getCanonicalText())){
            PsiClass psiClass = PsiUtils.findClassByName(itemField.getProject(), itemField.getType().getCanonicalText());
            if (psiClass==null) return "";
            Map<String, Object> result = new HashMap<>();
            for (PsiField field : psiClass.getAllFields()) {
                if (ParamUtils.isBaseType(field.getType().getCanonicalText())) {
                    if (ParamUtils.isString(field.getType().getCanonicalText()))
                        result.put(field.getName(), getTargetValue(field));
                }
            }
            return result;
        }
        //???
        return "";
    }
}
