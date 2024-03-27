/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * JacksonFieldAnnotationDescription.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        String defaultName = field.getName();
        PsiAnnotation annotation = AnnotationUtil.findAnnotation(field, "com.fasterxml.jackson.annotation.JsonProperty");
        if (annotation != null) {
            String value = ParamUtils.getAnnotationStringValue(annotation, "value");
            if (value != null) return value;
        }

        //尝试从类上提取
        PsiAnnotation jsonNaming = AnnotationUtil.findAnnotation(field.getContainingClass(), "com.fasterxml.jackson.databind.annotation.JsonNaming");
        if (jsonNaming == null) return defaultName;
        PsiAnnotationMemberValue psiAnnotationMemberValue = jsonNaming.findAttributeValue("value");
        if (psiAnnotationMemberValue == null) return defaultName;
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

        return defaultName;
    }
}
