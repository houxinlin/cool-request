package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.net.MediaTypes;
import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.hxl.plugin.springboot.invoke.springmvc.JSONObjectBody;
import com.hxl.plugin.springboot.invoke.springmvc.StringBody;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.deft.Obj;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

public class BodyParamSpeculate implements RequestParamSpeculate {
    private final Map<String, Supplier<Object>> defaultValueMap = new HashMap<>();

    public BodyParamSpeculate() {
        defaultValueMap.put(Date.class.getName(), Date::new);
        defaultValueMap.put(LocalDateTime.class.getName(), () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        defaultValueMap.put(LocalTime.class.getName(), () -> LocalTime.now().format(DateTimeFormatter.ISO_TIME));
        defaultValueMap.put(LocalDate.class.getName(), () -> LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        defaultValueMap.put(BigInteger.class.getName(), () -> 0);

    }

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        Map<String, Object> result = new HashMap<>();
        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(parameters)) {
            if (!ParamUtils.hasUserObject(method)) return;

            if (ParamUtils.hasSpringMvcRequestParamAnnotation(method) || ParamUtils.hasBaseType(method)) {
                new UrlParamSpeculate(true, false).set(method, httpRequestInfo);
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

        if (ParamUtils.isList(itemField)) {
            String listGenerics = ParamUtils.getListGenerics(itemField);
            if (listGenerics == null) return new ArrayList<>();

            PsiClass psiClass = PsiUtils.findClassByName(itemField.getProject(), listGenerics);
            return getObjectDefaultValue(psiClass);

        }
        if (defaultValueMap.containsKey(canonicalText)) {
            return defaultValueMap.get(canonicalText).get();
        }
        if (!ParamUtils.isJdkClass(canonicalText)) {
            PsiClass psiClass = PsiUtils.findClassByName(itemField.getProject(), itemField.getType().getCanonicalText());
            return getObjectDefaultValue(psiClass);
        }
        return "";
    }

    @NotNull
    private Object getObjectDefaultValue(PsiClass psiClass) {
        if (psiClass == null) return "";

        Map<String, Object> result = new HashMap<>();
        for (PsiField field : psiClass.getAllFields()) {
            if ("byte[]".equals(field.getType().getCanonicalText())) {
                result.put(field.getName(), new ArrayList<>());
                continue;
            }
            if (ParamUtils.isBaseType(field.getType().getCanonicalText())) {
                result.put(field.getName(), getTargetValue(field));
            }
        }
        return result;
    }

    static abstract class DefaultValue {
        private Object defaultValue;

        public DefaultValue(Supplier<Object> defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

}
