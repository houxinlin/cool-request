package com.cool.request.lib.springmvc.param;

import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.StringGuessBody;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

public class BodyParamSpeculate extends BasicBodySpeculate implements RequestParamSpeculate {
    private final Map<String, Supplier<Object>> defaultValueMap = new HashMap<>();
    private final List<FieldAnnotationDescription> fieldAnnotationDescriptions = new ArrayList<>();

    public BodyParamSpeculate() {
        defaultValueMap.put(Date.class.getName(), Date::new);
        defaultValueMap.put(LocalDateTime.class.getName(), () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        defaultValueMap.put(LocalTime.class.getName(), () -> LocalTime.now().format(DateTimeFormatter.ISO_TIME));
        defaultValueMap.put(LocalDate.class.getName(), () -> LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        defaultValueMap.put(BigInteger.class.getName(), () -> 0);
        fieldAnnotationDescriptions.add(new JacksonFieldAnnotationDescription());

    }

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        //非GET和具有表单的请求不设置此选项
        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(parameters)) {
//            if (!ParamUtils.hasUserObject(method)) return;
//            if (ParamUtils.hasSpringMvcRequestParamAnnotation(method) || ParamUtils.hasBaseType(method)) {
//                new UrlParamSpeculate(true, false).set(method, httpRequestInfo);
//            }
            PsiParameter requestBodyPsiParameter = ParamUtils.getParametersWithAnnotation(method, "org.springframework.web.bind.annotation.RequestBody");
            if (requestBodyPsiParameter == null) {
                //匹配到地一个用户数据类时候返回
                for (PsiParameter parameter : parameters) {
                    String canonicalText = parameter.getType().getCanonicalText();
                    if (ParamUtils.isSpringBoot(canonicalText)) continue;
                    if (ParamUtils.isHttpServlet(canonicalText)) continue;
                    if (ParamUtils.isUserObject(canonicalText)) {
                        PsiClass psiClass = PsiUtils.findClassByName(method.getProject(), ModuleUtil.findModuleForPsiElement(method).getName(), canonicalText);
                        if (psiClass == null) continue;
                        setRequestBody(psiClass, httpRequestInfo);
                        break;
                    }
                }
            } else {
                if (ParamUtils.isString(requestBodyPsiParameter.getType().getCanonicalText())) {
                    httpRequestInfo.setRequestBody(new StringGuessBody(""));
                    httpRequestInfo.setContentType(MediaTypes.TEXT);
                } else if (ParamUtils.isUserObject(requestBodyPsiParameter.getType().getCanonicalText())) {
                    PsiClass psiClass = PsiUtils.findClassByName(method.getProject(), ModuleUtil.findModuleForPsiElement(method).getName(), requestBodyPsiParameter.getType().getCanonicalText());
                    if (psiClass != null) setRequestBody(psiClass, httpRequestInfo);
                }

            }
            //如果是string类型
            if (parameters.length == 1 && ParamUtils.isString(parameters[0].getType().getCanonicalText())) {
                PsiAnnotation requestBody = parameters[0].getAnnotation("org.springframework.web.bind.annotation.RequestBody");
                if (requestBody != null) {
                    httpRequestInfo.setRequestBody(new StringGuessBody(""));
                    httpRequestInfo.setContentType(MediaTypes.TEXT);
                }
            }
        }
    }

}
