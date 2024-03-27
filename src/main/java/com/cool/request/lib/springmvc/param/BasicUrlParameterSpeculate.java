/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BasicUrlParameterSpeculate.java is part of Cool Request
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

import com.cool.request.lib.springmvc.ParameterAnnotationDescriptionUtils;
import com.cool.request.components.http.RequestParameterDescription;
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
