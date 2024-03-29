/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringMvcControllerScan.java is part of Cool Request
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

package com.cool.request.components.api.scans;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.lib.springmvc.ControllerAnnotation;
import com.cool.request.lib.springmvc.config.reader.UserProjectContextPathReader;
import com.cool.request.lib.springmvc.config.reader.UserProjectServerPortReader;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.PsiJaxRsUtils;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SpringMvcControllerScan {

    public List<Controller> scan(Project project) {
        List<Controller> result = new ArrayList<>();
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        for (Module module : moduleManager.getModules()) {
            UserProjectServerPortReader userProjectServerPortReader = new UserProjectServerPortReader(project, module);
            UserProjectContextPathReader userProjectContextPathReader = new UserProjectContextPathReader(project, module);

            Integer currentModuleServerPort = userProjectServerPortReader.read();
            String contextPath = userProjectContextPathReader.read();
            scanByAnnotation(project, module, result, currentModuleServerPort, contextPath, ControllerAnnotation.CONTROLLER);
            scanByAnnotation(project, module, result, currentModuleServerPort, contextPath, ControllerAnnotation.REST_CONTROLLER);
            scanByAnnotation(project, module, result, currentModuleServerPort, contextPath, ControllerAnnotation.JAX_RS_PATH);
        }

        result.sort(Comparator.comparing(Controller::getSimpleClassName));
        return result;
    }

    private void scanByAnnotation(Project project,
                                  Module module,
                                  List<Controller> result,
                                  Integer currentModuleServerPort,
                                  String contextPath, ControllerAnnotation controllerAnnotation) {
        Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(controllerAnnotation.getName(), project,
                GlobalSearchScope.moduleScope(module));
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            PsiElement psiAnnotationParent = psiAnnotation.getParent();
            if (!controllerAnnotation.getAnnotationName().equalsIgnoreCase(psiAnnotation.getQualifiedName()))
                continue;
            if (psiAnnotationParent == null) continue;
            if (!(psiAnnotationParent instanceof PsiModifierList)) continue;
            PsiElement psiElement = psiAnnotationParent.getParent();
            if (!(psiElement instanceof PsiClass)) {
                continue;
            }
            if (!PsiUtils.isAbstractClass(((PsiClass) psiElement))) {
                if (ControllerAnnotation.JAX_RS_PATH == controllerAnnotation) {
                    result.addAll(resolveJaxRsHttpRouteMethods((PsiClass) psiElement, module, currentModuleServerPort, contextPath));
                    continue;
                }
                result.addAll(resolveHttpRouteMethods((PsiClass) psiElement, module, currentModuleServerPort, contextPath));
            }

        }
    }

    private List<StaticController> resolveHttpRouteMethods(PsiClass originClass, Module module,
                                                           Integer currentModuleServerPort, String contextPath) {
        if (PsiUtils.isObjectClass(originClass)) return new ArrayList<>();

        List<StaticController> result = new ArrayList<>();
        for (PsiMethod psiMethod : originClass.getAllMethods()) {

            List<HttpMethod> httpMethod = PsiUtils.getHttpMethod(psiMethod);
            if (httpMethod.isEmpty()) continue;
            List<String> httpUrl = ParamUtils.getHttpUrl(originClass, psiMethod);
            if (httpUrl == null) continue;
            PsiClass superClassName = PsiUtils.getSuperClassName(psiMethod);
            for (String url : httpUrl) {
                // TODO: 2024/1/10 //这里有问题，先获取第一个
                StaticController controller = (StaticController) Controller.ControllerBuilder.aController()
                        .withHttpMethod(httpMethod.get(0).toString())
                        .withMethodName(psiMethod.getName())
                        .withContextPath(contextPath)
                        .withServerPort(currentModuleServerPort)
                        .withModuleName(module.getName())
                        .withUrl(StringUtils.addPrefixIfMiss(url, "/"))
                        .withSimpleClassName(originClass.getQualifiedName())
                        .withParamClassList(PsiUtils.getParamClassList(psiMethod))
                        .build(new StaticController(), module.getProject());
                controller.setSuperPsiClass(superClassName);
                result.add(controller);
                //这里可能是接口中的psiMethod

                controller.getOwnerPsiMethod().add(psiMethod);

                //可能是接口里面定义的,当前具有@Controller等注解的类和psiMethod不在同一个类
                if (psiMethod.getContainingClass() != null && psiMethod.getContainingClass() != originClass) {
                    PsiModifierList modifierList = psiMethod.getModifierList();
                    if (modifierList.hasModifierProperty(PsiModifier.ABSTRACT) && psiMethod.getBody() == null
                            && psiMethod.getContainingClass().isInterface()) {
                        PsiMethod[] methodsByName = originClass.findMethodsByName(psiMethod.getName(), false);
                        for (PsiMethod method : methodsByName) {
                            if (areSignaturesEqual(method, psiMethod)) {
                                controller.getOwnerPsiMethod().add(method);
                            }
                        }
                    }

                }
            }

        }
        return result;
    }

    private List<StaticController> resolveJaxRsHttpRouteMethods(PsiClass originClass, Module module,
                                                                Integer currentModuleServerPort, String contextPath) {
        if (PsiJaxRsUtils.isObjectClass(originClass)) return new ArrayList<>();

        List<StaticController> result = new ArrayList<>();
        for (PsiMethod psiMethod : originClass.getAllMethods()) {
            List<HttpMethod> httpMethod = PsiJaxRsUtils.getHttpMethod(psiMethod);
            if (httpMethod.isEmpty()) continue;
            List<String> httpUrl = PsiJaxRsUtils.getHttpUrl(originClass, psiMethod);
            if (httpUrl == null) continue;
            PsiClass superClassName = PsiJaxRsUtils.getSuperClassName(psiMethod);
            for (String url : httpUrl) {
                StaticController controller = (StaticController) Controller.ControllerBuilder.aController()
                        .withHttpMethod(httpMethod.get(0).toString())
                        .withMethodName(psiMethod.getName())
                        .withContextPath(contextPath)
                        .withServerPort(currentModuleServerPort)
                        .withModuleName(module.getName())
                        .withUrl(StringUtils.addPrefixIfMiss(url, "/"))
                        .withSimpleClassName(originClass.getQualifiedName())
                        .withParamClassList(PsiJaxRsUtils.getParamClassList(psiMethod))
                        .build(new StaticController(), module.getProject());
                controller.setSuperPsiClass(superClassName);
                result.add(controller);
                //这里可能是接口中的psiMethod
                controller.getOwnerPsiMethod().add(psiMethod);

                //可能是接口里面定义的,当前具有@Controller等注解的类和psiMethod不在同一个类
                if (psiMethod.getContainingClass() != null && psiMethod.getContainingClass() != originClass) {
                    PsiModifierList modifierList = psiMethod.getModifierList();
                    if (modifierList != null &&
                            modifierList.hasModifierProperty(PsiModifier.ABSTRACT) &&
                            psiMethod.getBody() == null && psiMethod.getContainingClass().isInterface()) {

                        PsiMethod[] methodsByName = originClass.findMethodsByName(psiMethod.getName(), false);
                        for (PsiMethod method : methodsByName) {
                            if (areSignaturesEqual(method, psiMethod)) {
                                controller.getOwnerPsiMethod().add(method);
                            }
                        }
                    }

                }
            }

        }
        return result;
    }

    public static boolean areSignaturesEqual(PsiMethod method1, PsiMethod method2) {
        // 检查方法名是否一致
        if (!method1.getName().equals(method2.getName())) {
            return false;
        }

        // 检查返回类型是否一致
        if (!areTypesEqual(method1.getReturnType(), method2.getReturnType())) {
            return false;
        }

        // 检查参数列表是否一致
        PsiParameter[] parameters1 = method1.getParameterList().getParameters();
        PsiParameter[] parameters2 = method2.getParameterList().getParameters();

        if (parameters1.length != parameters2.length) {
            return false;
        }

        for (int i = 0; i < parameters1.length; i++) {
            if (!areTypesEqual(parameters1[i].getType(), parameters2[i].getType())) {
                return false;
            }
        }

        return true;
    }

    private static boolean areTypesEqual(PsiType type1, PsiType type2) {
        // 这里可以根据需要扩展，比如考虑类型的泛型参数等
        return type1.equals(type2);
    }
}
