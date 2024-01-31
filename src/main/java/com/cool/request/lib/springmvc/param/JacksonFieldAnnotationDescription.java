package com.cool.request.lib.springmvc.param;

import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiField;

public class JacksonFieldAnnotationDescription implements FieldAnnotationDescription {
    @Override
    public String getRelaName(PsiField field) {
        //优先JsonProperty注解
        PsiAnnotation annotation = AnnotationUtil.findAnnotation(field, "com.fasterxml.jackson.annotation.JsonProperty");
        if (annotation != null) {
            String value = ParamUtils.getAnnotationStringValue(annotation, "value");
            if (value != null) return value;
        }

        //尝试从类上提取
        PsiAnnotation jsonNaming = AnnotationUtil.findAnnotation(field.getContainingClass(), "com.fasterxml.jackson.databind.annotation.JsonNaming");
        PsiAnnotationMemberValue psiAnnotationMemberValue = jsonNaming.findAttributeValue("value");
        if (psiAnnotationMemberValue instanceof PsiClassObjectAccessExpression) {
            String text = psiAnnotationMemberValue.getText();
            if ("PropertyNamingStrategy.SnakeCaseStrategy.class".equals(text) ||
                    "PropertyNamingStrategies.SnakeCaseStrategy.class".equals(text)) {
                return PropertyNamingStrategies.SnakeCaseStrategy.INSTANCE.translate(field.getName());
            }
            if ("PropertyNamingStrategies.KebabCaseStrategy.class".equals(text) ||
                    "PropertyNamingStrategy.KebabCaseStrategy.class".equals(text)) {
                return PropertyNamingStrategies.KebabCaseStrategy.INSTANCE.translate(field.getName());
            }
            if ("PropertyNamingStrategy.LowerCaseStrategy.class".equals(text) ||
                    "PropertyNamingStrategies.LowerCaseStrategy.class".equals(text)) {
                return PropertyNamingStrategies.LowerCaseStrategy.INSTANCE.translate(field.getName());
            }
            if ("PropertyNamingStrategy.UpperCamelCaseStrategy.class".equals(text) ||
                    "PropertyNamingStrategies.UpperCamelCaseStrategy.class".equals(text)) {
                return PropertyNamingStrategies.UpperCamelCaseStrategy.INSTANCE.translate(field.getName());
            }
            if ("PropertyNamingStrategies.LowerDotCaseStrategy.class".equals(text)) {
                return PropertyNamingStrategies.LowerCamelCaseStrategy.INSTANCE.translate(field.getName());
            }
        }

        return null;
    }
}
