/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StaticControllerBuilder.java is part of Cool Request
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
import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class StaticControllerBuilder {
    public static List<StaticController> build(List<String> urls,
                                               HttpMethod httpMethod,
                                               PsiMethod psiMethod,
                                               String contextPath,
                                               int serverPort,
                                               Module module,
                                               PsiClass originClass) {
        List<StaticController> result = new ArrayList<>();
        for (String url : urls) {
            StaticController controller = (StaticController) Controller.ControllerBuilder.aController()
                    .withHttpMethod(httpMethod.toString())
                    .withMethodName(psiMethod.getName())
                    .withContextPath(contextPath)
                    .withServerPort(serverPort)
                    .withModuleName(module.getName())
                    .withUrl(StringUtils.addPrefixIfMiss(url, "/"))
                    .withSimpleClassName(originClass.getQualifiedName())
                    .withParamClassList(PsiUtils.getParamClassList(psiMethod))
                    .build(new StaticController(), module.getProject());
            result.add(controller);
        }
        return result;

    }
}
