/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * PathParamSpeculate.java is part of Cool Request
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

import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.components.http.RequestParameterDescription;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;

public class PathParamSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        List<RequestParameterDescription> param = new ArrayList<>();

        for (PsiParameter parameter : listCanSpeculateParam(method)) {
            PsiAnnotation annotation = AnnotationUtil.findAnnotation(parameter, "org.springframework.web.bind.annotation.PathVariable");
            if (annotation == null) continue;
            String value = ParamUtils.getAnnotationStringValue(annotation, "value");
            if (!StringUtils.isEmpty(value)) {
                param.add(new RequestParameterDescription(value, "text", ""));
            } else {
                param.add(new RequestParameterDescription(parameter.getName(), "text", ""));
            }
        }
        httpRequestInfo.setPathParams(param);
    }
}
