/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * UrlParamSpeculate.java is part of Cool Request
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

package com.cool.request.lib.rose;

import com.cool.request.components.http.RequestParameterDescription;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.param.RequestParamSpeculate;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

public class UrlParamSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("net.paoding.rose.web.annotation.Param");
            if (requestParam != null) {
                String requestParamValue = ParamUtils.getAnnotationStringValue(requestParam, "value");
                if (!StringUtils.isEmpty(requestParamValue)) {
                    String type = ParamUtils.getParameterType(parameter);
                    httpRequestInfo.getUrlParams().add(new RequestParameterDescription(requestParamValue, type, ""));
                }
            }
        }
    }
}
