/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * JaxRsControllerConverter.java is part of Cool Request
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

package com.cool.request.scan.rose;

import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.scan.ControllerConverter;
import com.cool.request.scan.StaticControllerBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class RoseControllerConverter implements ControllerConverter {

    private final RoseHttpMethodDefinition roseHttpMethodDefinition = new RoseHttpMethodDefinition();

    @Override
    public List<HttpMethod> parseHttpMethod(PsiMethod psiMethod) {
        return roseHttpMethodDefinition.parseHttpMethod(psiMethod);
    }

    @Override
    public boolean canConverter(PsiMethod psiMethod) {
        return !roseHttpMethodDefinition.parseHttpMethod(psiMethod).isEmpty();
    }

    @Override
    public List<StaticController> psiMethodToController(Project project, PsiClass originClass, Module module, PsiMethod psiMethod) {
        List<HttpMethod> httpMethods = parseHttpMethod(psiMethod);
        if (httpMethods.isEmpty()) return new ArrayList<>();
        List<String> urls = roseHttpMethodDefinition.parseHttpUrl(originClass, psiMethod);
        return StaticControllerBuilder.build(project,urls, httpMethods.get(0), psiMethod, "", 8080, module, originClass);
    }

}
