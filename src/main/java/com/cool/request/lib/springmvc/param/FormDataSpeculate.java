/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FormDataSpeculate.java is part of Cool Request
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
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FormDataSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        //无法推测其余参数是不是form-data，只有存在file的时候可以断定是form-data请求
        if (!ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;
        List<FormDataInfo> param = new ArrayList<>();
        for (PsiParameter parameter : listCanSpeculateParam(method)) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            //将所有参数都放在multipart/form-data里面

            Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
            String value = psiAnnotationValues.get("value");
            if (StringUtils.isEmpty(value)) value = parameter.getName();
            FormDataInfo formDataInfo = new FormDataInfo(value, "", ParamUtils.isMultipartFile(parameter) ? "file" : "text");
            String parameterDescription = AllMethodDescriptionParse.getInstance().parseParameterDescription(parameter);
            formDataInfo.setDescription(Optional.ofNullable(parameterDescription).orElse(""));
            param.add(formDataInfo);
        }
        if (!param.isEmpty()) {
            httpRequestInfo.setContentType(MediaTypes.MULTIPART_FORM_DATA);
            List<FormDataInfo> formDataInfos = httpRequestInfo.getFormDataInfos();
            formDataInfos.addAll(param);
        }
    }
}
