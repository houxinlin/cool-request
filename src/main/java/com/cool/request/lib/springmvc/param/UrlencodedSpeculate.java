/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * UrlencodedSpeculate.java is part of Cool Request
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
import com.cool.request.components.http.RequestParameterDescription;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.scan.spring.SpringMvcHttpMethodDefinition;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class UrlencodedSpeculate extends BasicUrlParameterSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        //比如是非GET情况，没有MultipartFile文件
        if (!SpringMvcHttpMethodDefinition.isGetRequest(method) &&
                !ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) {
            //有RequestBody注解
            if (ParamUtils.hasRequestBody(method)) return;

            List<RequestParameterDescription> param = new ArrayList<>(super.get(method, false));
            if (!param.isEmpty()) {
                httpRequestInfo.setUrlencodedBody(param);
                httpRequestInfo.setContentType(MediaTypes.APPLICATION_WWW_FORM);
            }
        }
    }
}
