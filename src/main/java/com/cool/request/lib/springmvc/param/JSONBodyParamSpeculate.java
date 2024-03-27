/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * JSONBodyParamSpeculate.java is part of Cool Request
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
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.List;

public class JSONBodyParamSpeculate extends BasicBodySpeculate implements RequestParamSpeculate {
    public JSONBodyParamSpeculate() {
    }

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        List<PsiParameter> parameters = listCanSpeculateParam(method);
        //非GET和具有表单的请求不设置此选项
        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(parameters)) {
            //没有RequestBody注解，不要设置任何JSON参数
            PsiParameter requestBodyPsiParameter = ParamUtils.getParametersWithAnnotation(method, "org.springframework.web.bind.annotation.RequestBody");
            if (requestBodyPsiParameter != null) {
                PsiClass psiClass = PsiUtils.findClassByName(method.getProject(),
                        ModuleUtil.findModuleForPsiElement(method).getName(),
                        requestBodyPsiParameter.getType().getCanonicalText());
                if (psiClass != null) setJSONRequestBody(psiClass, httpRequestInfo);
            }
        }
    }

}
