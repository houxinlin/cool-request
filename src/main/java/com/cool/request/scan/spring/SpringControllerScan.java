/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringControllerScan.java is part of Cool Request
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
import com.cool.request.lib.springmvc.ControllerAnnotation;
import com.cool.request.scan.BaseControllerScan;
import com.cool.request.scan.ControllerScan;
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

public class SpringControllerScan extends BaseControllerScan implements ControllerScan {
    public SpringControllerScan() {
        super(new SpringMvcControllerConverter());
    }

    @Override
    public List<Controller> scanController(Project project) {
        return scan(project);
    }

    private List<Controller> scan(Project project) {
        List<Controller> result = new ArrayList<>();
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        for (Module module : moduleManager.getModules()) {

            scanByAnnotation(project, module, result, ControllerAnnotation.CONTROLLER);
            scanByAnnotation(project, module, result, ControllerAnnotation.REST_CONTROLLER);
//            scanByAnnotation(project, module, result, currentModuleServerPort, contextPath, ControllerAnnotation.JAX_RS_PATH);
        }

        result.sort(Comparator.comparing(Controller::getSimpleClassName));
        return result;
    }

    private void scanByAnnotation(Project project,
                                  Module module,
                                  List<Controller> result,
                                  ControllerAnnotation controllerAnnotation) {
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
                result.addAll(resolveHttpRouteMethods((PsiClass) psiElement, module));
            }
        }
    }

    private List<StaticController> resolveHttpRouteMethods(PsiClass originClass, Module module) {
        if (PsiUtils.isObjectClass(originClass)) return new ArrayList<>();

        List<StaticController> result = new ArrayList<>();
        //这里只能使用getAllMethods，有些用户喜欢把api定义写在接口里面，标有@RestController类的方法下都不存在@GetMapping
        //所以需要把originClass类传递过去，方便提取原始类中的@RequestMapping信息
        for (PsiMethod psiMethod : originClass.getAllMethods()) {
            List<StaticController> controllers = getControllerConverter().
                    psiMethodToController(originClass, module, psiMethod);
            if (controllers != null) result.addAll(controllers);
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
