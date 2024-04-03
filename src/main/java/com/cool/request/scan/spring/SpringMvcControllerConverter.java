/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringMvcControllerConverter.java is part of Cool Request
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

package com.cool.request.scan.spring;

import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.lib.springmvc.config.reader.PropertiesReader;
import com.cool.request.scan.ControllerConverter;
import com.cool.request.scan.StaticControllerBuilder;
import com.intellij.codeInsight.navigation.MethodImplementationsSearch;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.List;

public class SpringMvcControllerConverter implements ControllerConverter {
    private SpringMvcHttpMethodDefinition springMvcHttpMethodDefinition = new SpringMvcHttpMethodDefinition();

    @Override
    public boolean canConverter(PsiMethod psiMethod) {
        return !parseHttpMethod(psiMethod).isEmpty();
    }

    @Override
    public List<StaticController> psiMethodToController(Project project, PsiClass originClass,
                                                        Module module,
                                                        PsiMethod psiMethod) {
        if (isInterfaceMethod(psiMethod)) {
            List<StaticController> result = new ArrayList<>();
            List<PsiMethod> methods = new ArrayList<>();
            MethodImplementationsSearch.getOverridingMethods(psiMethod, methods, GlobalSearchScope.allScope(project));
            for (PsiMethod implementation : methods) {
                List<StaticController> parsed = parse(project, implementation.getContainingClass(),
                        ModuleUtil.findModuleForPsiElement(implementation), implementation);
                result.addAll(parsed);
            }
            return result;
        }

        return parse(project, originClass, module, psiMethod);
    }

    @Override
    public List<HttpMethod> parseHttpMethod(PsiMethod psiMethod) {
        List<HttpMethod> httpMethods = springMvcHttpMethodDefinition.parseHttpMethod(psiMethod);
        if (httpMethods.isEmpty()) {
            for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
                List<HttpMethod> supperHttpMethods = springMvcHttpMethodDefinition.parseHttpMethod(superMethod);
                if (!supperHttpMethods.isEmpty()) return supperHttpMethods;
            }
        }
        return httpMethods;

    }

    private List<String> parseHttpUrl(PsiClass originClass, PsiMethod psiMethod) {
        List<String> httpUrl = springMvcHttpMethodDefinition.parseHttpUrl(originClass, psiMethod);
        if (httpUrl.isEmpty()) {
            for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
                List<String> supperHttpMethods = springMvcHttpMethodDefinition.parseHttpUrl(originClass, superMethod);
                if (!supperHttpMethods.isEmpty()) return supperHttpMethods;
            }
        }
        return httpUrl;

    }

    private List<StaticController> parse(Project project,
                                         PsiClass originClass,
                                         Module module,
                                         PsiMethod psiMethod) {
        PropertiesReader propertiesReader = new PropertiesReader();
        List<HttpMethod> httpMethods = parseHttpMethod(psiMethod);
        List<String> httpUrl = parseHttpUrl(originClass, psiMethod);

        String contextPath = propertiesReader.readContextPath(project, module);
        int serverPort = propertiesReader.readServerPort(project, module);

        if (httpMethods.isEmpty() || httpUrl.isEmpty()) return new ArrayList<>();

        return StaticControllerBuilder.build(httpUrl, httpMethods.get(0), psiMethod, contextPath, serverPort, module, originClass);
    }

    public boolean isInterfaceMethod(PsiMethod method) {
        // 获取方法所在的类
        PsiClass containingClass = method.getContainingClass();

        // 检查类是否为接口
        if (containingClass != null && containingClass.isInterface()) {
            // 检查方法是否是接口中的方法
            return !method.hasModifierProperty(PsiModifier.DEFAULT) && !method.hasModifierProperty(PsiModifier.STATIC);
        }

        return false;
    }
}
