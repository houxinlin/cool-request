/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FormDataRequestPartSpeculate.java is part of Cool Request
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

import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.scan.doc.AllMethodDescriptionParse;
import com.cool.request.utils.StringUtils;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormDataRequestPartSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        if (!ParamUtils.hasRequestPart(method.getParameterList().getParameters())) return;
        List<FormDataInfo> param = new ArrayList<>();
        for (PsiParameter parameter : listCanSpeculateParam(method)) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestPart");
            PsiType type = parameter.getType();
            if (type instanceof PsiArrayType) {
                PsiType componentType = ((PsiArrayType) type).getComponentType();
                if (componentType instanceof PsiPrimitiveType) {
                    if ("byte".equalsIgnoreCase(((PsiPrimitiveType) componentType).getName())) {
                        String name = ParamUtils.getAnnotationStringValue(requestParam, "value");
                        FormDataInfo formDataInfo = new FormDataInfo(StringUtils.isEmpty(name) ? parameter.getName() : name, "file", "");
                        String parameterDescription = AllMethodDescriptionParse.getInstance().parseParameterDescription(parameter);
                        formDataInfo.setDescription(Optional.ofNullable(parameterDescription).orElse(""));
                        param.add(formDataInfo);
                    }
                }
            }
            if (!param.isEmpty()) {
                httpRequestInfo.setContentType(MediaTypes.MULTIPART_FORM_DATA);
                List<FormDataInfo> formDataInfos = httpRequestInfo.getFormDataInfos();
                formDataInfos.addAll(param);
            }
        }
    }
}
