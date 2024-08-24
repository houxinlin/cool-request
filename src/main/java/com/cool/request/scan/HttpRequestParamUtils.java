/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HttpRequestParamUtils.java is part of Cool Request
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

package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.CustomController;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.param.RequestParamSpeculate;
import com.cool.request.scan.jaxrs.JaxRsRequestParamSpeculate;
import com.cool.request.scan.rose.RoseRequestParamSpeculate;
import com.cool.request.scan.spring.SpringMvcRequestParamSpeculate;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.Arrays;
import java.util.List;

public class HttpRequestParamUtils {
    private static final List<RequestParamSpeculate> requestParamSpeculate =
            Arrays.asList(new SpringMvcRequestParamSpeculate(), new JaxRsRequestParamSpeculate(), new RoseRequestParamSpeculate());

    public static HttpRequestInfo getHttpRequestInfo(Project project, Controller controller) {
        if (controller instanceof CustomController) return new HttpRequestInfo();
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
        PsiMethod psiMethod = Scans.getInstance(project).controllerToPsiMethod(project, controller);
        if (psiMethod != null) {
            for (RequestParamSpeculate paramSpeculate : requestParamSpeculate) {
                paramSpeculate.set(psiMethod, httpRequestInfo);
            }
        }
        return httpRequestInfo;
    }
}
