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

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.lib.springmvc.config.reader.UserProjectContextPathReader;
import com.cool.request.lib.springmvc.config.reader.UserProjectServerPortReader;
import com.cool.request.scan.ControllerConverter;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
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
    private final SpringMvcHttpMethodParser springMvcHTTPMethodParser = new SpringMvcHttpMethodParser();
    private final SpringPathParser springPathParser = new SpringPathParser();

    @Override
    public List<StaticController> psiMethodToController(PsiClass originClass,
                                                        Module module,
                                                        PsiMethod psiMethod) {
        Project project = psiMethod.getProject();
        if (isInterfaceMethod(psiMethod)) {
            List<StaticController> result = new ArrayList<>();
            List<PsiMethod> methods = new ArrayList<>();
            MethodImplementationsSearch.getOverridingMethods(psiMethod, methods, GlobalSearchScope.allScope(project));
            for (PsiMethod implementation : methods) {
                List<StaticController> parsed = parse(implementation.getContainingClass(), ModuleUtil.findModuleForPsiElement(implementation), implementation);
                result.addAll(parsed);
            }
            return result;
        }

        return parse(originClass, module, psiMethod);
    }

    private List<HttpMethod> parseHttpMethod(PsiMethod psiMethod) {
        List<HttpMethod> httpMethods = springMvcHTTPMethodParser.parserHttpMethod(psiMethod);
        if (httpMethods.isEmpty()) {
            for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
                List<HttpMethod> supperHttpMethods = springMvcHTTPMethodParser.parserHttpMethod(superMethod);
                if (!supperHttpMethods.isEmpty()) return supperHttpMethods;
            }
        }
        return httpMethods;

    }

    private List<String> parseHttpUrl(PsiClass originClass, PsiMethod psiMethod) {
        List<String> httpUrl = springPathParser.parserPath(originClass, psiMethod);
        if (httpUrl.isEmpty()) {
            for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
                List<String> supperHttpMethods = springPathParser.parserPath(originClass, superMethod);
                if (!supperHttpMethods.isEmpty()) return supperHttpMethods;
            }
        }
        return httpUrl;

    }

    private List<StaticController> parse(PsiClass originClass,
                                         Module module,
                                         PsiMethod psiMethod) {
        UserProjectServerPortReader userProjectServerPortReader = new UserProjectServerPortReader(module.getProject(), module);
        UserProjectContextPathReader userProjectContextPathReader = new UserProjectContextPathReader(module.getProject(), module);
        List<HttpMethod> httpMethods = parseHttpMethod(psiMethod);
        List<String> httpUrl = parseHttpUrl(originClass, psiMethod);

        if (httpMethods.isEmpty() || httpUrl.isEmpty()) return new ArrayList<>();
        List<StaticController> result = new ArrayList<>();
        for (String url : httpUrl) {
            StaticController controller = (StaticController) Controller.ControllerBuilder.aController()
                    .withHttpMethod(httpMethods.get(0).toString())
                    .withMethodName(psiMethod.getName())
                    .withContextPath(userProjectContextPathReader.read())
                    .withServerPort(userProjectServerPortReader.read())
                    .withModuleName(module.getName())
                    .withUrl(StringUtils.addPrefixIfMiss(url, "/"))
                    .withSimpleClassName(originClass.getQualifiedName())
                    .withParamClassList(PsiUtils.getParamClassList(psiMethod))
                    .build(new StaticController(), module.getProject());
            result.add(controller);
        }
        return result;
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

    @Override
    public PsiMethod controllerToPsiMethod(Project project, Controller controller) {
        PsiClass psiClass = PsiUtils.findClassByName(project, controller.getModuleName(), controller.getSimpleClassName());
        if (psiClass != null) {
            PsiMethod[] methodsByName = psiClass.findMethodsByName(controller.getMethodName(), true);
            for (PsiMethod psiMethod : methodsByName) {
                List<StaticController> staticControllers = psiMethodToController(psiClass, ModuleUtil.findModuleForPsiElement(psiMethod), psiMethod);
                for (StaticController staticController : staticControllers) {
                    if (StringUtils.isEqualsIgnoreCase(staticController.getId(), controller.getId())) {
                        return psiMethod;
                    }
                }
            }
        }
        return null;
    }
}
