/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringMvcRequestMapping.java is part of Cool Request
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

package com.cool.request.lib.springmvc;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.CustomController;
import com.cool.request.lib.springmvc.param.*;
import com.cool.request.scan.spring.SpringMvcControllerConverter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class SpringMvcRequestMapping {
    private final List<RequestParamSpeculate> requestParamSpeculates = new ArrayList<>();

    public SpringMvcRequestMapping() {
        //第一个必须是UrlencodedSpeculate
        requestParamSpeculates.add(new UrlencodedSpeculate());
        requestParamSpeculates.add(new UrlParamSpeculate());
        requestParamSpeculates.add(new HeaderParamSpeculate());
        requestParamSpeculates.add(new JSONBodyParamSpeculate());
        requestParamSpeculates.add(new FormDataSpeculate());

        requestParamSpeculates.add(new PathParamSpeculate());
        requestParamSpeculates.add(new ResponseBodySpeculate());
        requestParamSpeculates.add(new StringBodyParamSpeculate());
    }

    public HttpRequestInfo getHttpRequestInfo(Project project, Controller controller) {
        if (controller instanceof CustomController) return new HttpRequestInfo();
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
        PsiMethod psiMethod = new SpringMvcControllerConverter().controllerToPsiMethod(project, controller);
        if (psiMethod != null) {
            for (RequestParamSpeculate requestParamSpeculate : requestParamSpeculates) {
                try {
                    requestParamSpeculate.set(psiMethod, httpRequestInfo);
                } catch (Exception ignored) {
                }
            }
        }
        return httpRequestInfo;
    }
}
