/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StringBodyParamSpeculate.java is part of Cool Request
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

import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.StringGuessBody;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.scan.spring.SpringMvcHttpMethodDefinition;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.List;

public class StringBodyParamSpeculate extends BasicBodySpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        List<PsiParameter> parameters = listCanSpeculateParam(method);
        //非GET和具有表单的请求不设置此选项
        if (httpRequestInfo.getContentType() != null) return;
        if (!SpringMvcHttpMethodDefinition.isGetRequest(method) && !ParamUtils.hasMultipartFile(parameters)) {
            if (parameters.size() == 1 && ParamUtils.isString(parameters.get(0).getType().getCanonicalText())) {
                httpRequestInfo.setRequestBody(new StringGuessBody(""));
                httpRequestInfo.setContentType(MediaTypes.TEXT);
                return;
            }

            //没有RequestBody注解，不要设置任何参数
            PsiParameter requestBodyPsiParameter = ParamUtils.getParametersWithAnnotation(method, "org.springframework.web.bind.annotation.RequestBody");
            if (requestBodyPsiParameter != null) {
                if (ParamUtils.isString(requestBodyPsiParameter.getType().getCanonicalText())) {
                    httpRequestInfo.setRequestBody(new StringGuessBody(""));
                    httpRequestInfo.setContentType(MediaTypes.TEXT);
                }
            }
        }
    }
}
