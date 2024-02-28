package com.cool.request.lib.springmvc.param;

import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.springmvc.GuessBody;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.JSONObjectGuessBody;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

public abstract class BasicBodySpeculate {
    private final List<FieldAnnotationDescription> fieldAnnotationDescriptions = new ArrayList<>();
    private final Map<String, Supplier<Object>> defaultValueMap = new HashMap<>();

    public BasicBodySpeculate() {
        fieldAnnotationDescriptions.add(new JacksonFieldAnnotationDescription());
        defaultValueMap.put(Date.class.getName(), Date::new);
        defaultValueMap.put(LocalDateTime.class.getName(), () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        defaultValueMap.put(LocalTime.class.getName(), () -> LocalTime.now().format(DateTimeFormatter.ISO_TIME));
        defaultValueMap.put(LocalDate.class.getName(), () -> LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        defaultValueMap.put(BigInteger.class.getName(), () -> 0);
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

    protected GuessBody getGuessBody(PsiClass psiClass) {
        Map<String, Object> result = new HashMap<>();
        for (PsiField field : listCanApplyJsonField(psiClass)) {
            String fieldName = null;
            for (FieldAnnotationDescription fieldAnnotationDescription : fieldAnnotationDescriptions) {
                fieldName = fieldAnnotationDescription.getRelaName(field);
            }
            if (fieldName == null) fieldName = field.getName();
            result.put(fieldName, getTargetValue(field, new ArrayList<>()));
        }
        return new JSONObjectGuessBody(result);
    }

    protected void setRequestBody(PsiClass psiClass, HttpRequestInfo httpRequestInfo) {
        if (!PsiUtils.hasExist(psiClass.getProject(), psiClass)) return;
        httpRequestInfo.setRequestBody(getGuessBody(psiClass));
        httpRequestInfo.setContentType(MediaTypes.APPLICATION_JSON);
    }

    protected void setResponseBody(PsiClass psiClass, HttpRequestInfo httpRequestInfo) {
        if (!PsiUtils.hasExist(psiClass.getProject(), psiClass)) return;
        if (!ParamUtils.isUserObject(psiClass.getQualifiedName())) return;
        httpRequestInfo.setResponseBody(getGuessBody(psiClass));
        httpRequestInfo.setContentType(MediaTypes.APPLICATION_JSON);
    }

    protected Object getTargetValue(PsiField itemField, List<String> cache) {
        String canonicalText = itemField.getType().getCanonicalText();

        Object defaultValueByClassName = ParamUtils.getDefaultValueByClassName(canonicalText, null);
        if (defaultValueByClassName != null) return defaultValueByClassName;
        if (ParamUtils.isMap(itemField)) return Collections.EMPTY_MAP;
        if (ParamUtils.isArray(canonicalText) || ParamUtils.isList(itemField)) {
            String className = null;

            if (ParamUtils.isArray(canonicalText)) {
                className = canonicalText.replace("[]", "");
            } else {
                className = ParamUtils.getListGenerics(itemField);
                if (className == null) return new ArrayList<>();
            }

            if (ParamUtils.isUserObject(className)) {
                PsiClass psiClass = PsiUtils.findClassByName(itemField.getProject(), ModuleUtil.findModuleForPsiElement(itemField), className);
                if (psiClass != null) {
                    if (cache.contains(psiClass.getQualifiedName())) return new HashMap<>();
                    cache.add(psiClass.getQualifiedName());
                    return List.of(getObjectDefaultValue(psiClass, cache));
                }
                return new ArrayList<>();
            }
            if (ParamUtils.isBaseType(className)) return List.of(ParamUtils.getDefaultValueByClassName(className, ""));
            if (defaultValueMap.containsKey(className)) return List.of(defaultValueMap.get(canonicalText).get());
            return List.of();
        }

        if (defaultValueMap.containsKey(canonicalText)) {
            return defaultValueMap.get(canonicalText).get();
        }
        if (!ParamUtils.isJdkClass(canonicalText)) {
            PsiClass psiClass = PsiUtils.findClassByName(itemField.getProject(),
                    ModuleUtil.findModuleForPsiElement(itemField).getName(), itemField.getType().getCanonicalText());
            if (cache.contains(canonicalText)) return new HashMap<>();
            cache.add(canonicalText);
            return getObjectDefaultValue(psiClass, cache);
        }
        return "";
    }

    @NotNull
    protected Object getObjectDefaultValue(PsiClass psiClass, List<String> cache) {
        if (psiClass == null) return "";
        Map<String, Object> result = new HashMap<>();
        for (PsiField field : psiClass.getAllFields()) {
            String fieldName = null;
            for (FieldAnnotationDescription fieldAnnotationDescription : fieldAnnotationDescriptions) {
                fieldName = fieldAnnotationDescription.getRelaName(field);
            }
            if (fieldName == null) fieldName = field.getName();
            result.put(fieldName, getTargetValue(field, cache));
//            result.put(field.getName(), getTargetValue(field, cache));
        }
        return result;
    }
}
