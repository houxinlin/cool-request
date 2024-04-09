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

package com.cool.request.lib.springmvc.param;

import com.cool.request.components.http.RequestParameterDescription;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.scan.spring.SpringMvcHttpMethodDefinition;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class UrlParamSpeculate extends BasicUrlParameterSpeculate implements RequestParamSpeculate {
    public UrlParamSpeculate() {

    }

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        //如果不是Get请求则退出
        if (ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;
        if (SpringMvcHttpMethodDefinition.isNotGetRequest(method)) {
            //如果不是GET请求，并且请求体不是APPLICATION_WWW_FORM
            if (!MediaTypes.APPLICATION_WWW_FORM.equalsIgnoreCase(httpRequestInfo.getContentType())) {
                httpRequestInfo.setUrlParams(super.get(method, true));
            }
        } else {
            List<RequestParameterDescription> requestParameterDescriptions = super.get(method, false);
            httpRequestInfo.setUrlParams(requestParameterDescriptions);
        }
    }
}
