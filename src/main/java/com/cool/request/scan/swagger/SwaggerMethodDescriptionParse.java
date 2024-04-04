/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SwaggerMethodDecriptionParse.java is part of Cool Request
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

package com.cool.request.scan.swagger;

import com.cool.request.lib.springmvc.MethodDescription;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.scan.MethodDescriptionParse;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SwaggerMethodDescriptionParse implements MethodDescriptionParse {
    private static final SwaggerMethodDescriptionParse INSTANCE = new SwaggerMethodDescriptionParse();

    public static SwaggerMethodDescriptionParse getInstance() {
        return INSTANCE;
    }

    private static final List<MethodDescriptionParse> PARSE = Arrays.asList(
            new SwaggerV2MethodDescriptionParse(),
            new SwaggerV1MethodDescriptionParse());

    private static MethodDescription doParse(PsiMethod psiMethod, String annotationName, String description, String summary) {
        if (psiMethod == null) return null;
        MethodDescription methodDescription = new MethodDescription();
        PsiAnnotation psiAnnotation = psiMethod.getAnnotation(annotationName);
        if (psiAnnotation != null) {
            String descriptionValue = ParamUtils.getAnnotationStringValue(psiAnnotation, description);
            methodDescription.setDescription(Optional.ofNullable(descriptionValue).orElse(""));

            String summaryValue = ParamUtils.getAnnotationStringValue(psiAnnotation, summary);
            methodDescription.setSummary(Optional.ofNullable(summaryValue).orElse(""));
            methodDescription.setMethodName(psiMethod.getName());
            return methodDescription;
        }
        return null;
    }

    @Override
    public String parseParameterDescription(PsiParameter psiParameter) {
        return parseParameterDescription(psiParameter, "");
    }

    @Override
    public String parseParameterDescription(PsiParameter psiParameter, String defaultValue) {
        for (MethodDescriptionParse methodDescriptionParse : PARSE) {
            String parameterDescription = methodDescriptionParse.parseParameterDescription(psiParameter);
            if (parameterDescription != null) return parameterDescription;
        }
        return null;
    }

    @Override
    public MethodDescription parseMethodDescription(PsiMethod psiMethod) {
        for (MethodDescriptionParse methodDescriptionParse : PARSE) {
            MethodDescription methodDescription = methodDescriptionParse.parseMethodDescription(psiMethod);
            if (methodDescription != null) return methodDescription;
        }
        return null;
    }

    static class SwaggerV2MethodDescriptionParse implements MethodDescriptionParse {
        @Override
        public MethodDescription parseMethodDescription(PsiMethod psiMethod) {
            return doParse(
                    psiMethod,
                    "io.swagger.v3.oas.annotations.Operation",
                    "description",
                    "summary");
        }

        @Override
        public String parseParameterDescription(PsiParameter parameter, String defaultValue) {
            if (parameter == null) return defaultValue;
            PsiAnnotation psiAnnotation = parameter.getAnnotation("io.swagger.v3.oas.annotations.Parameter");
            if (psiAnnotation != null) {
                return ParamUtils.getAnnotationStringValue(psiAnnotation, "description");
            }
            return defaultValue;
        }

        @Override
        public String parseParameterDescription(PsiParameter parameter) {
            return parseParameterDescription(parameter, "");
        }
    }

    static class SwaggerV1MethodDescriptionParse implements MethodDescriptionParse {
        @Override
        public MethodDescription parseMethodDescription(PsiMethod psiMethod) {
            return doParse(
                    psiMethod,
                    "io.swagger.annotations.ApiOperation",
                    "notes",
                    "value");
        }

        @Override
        public String parseParameterDescription(PsiParameter psiParameter, String defaultValue) {
            return defaultValue;
        }

        @Override
        public String parseParameterDescription(PsiParameter psiParameter) {
            return parseParameterDescription(psiParameter, "");
        }
    }
}
