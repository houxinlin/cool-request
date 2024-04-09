/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * AnnotationControllerScan.java is part of Cool Request
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
import com.cool.request.lib.springmvc.ControllerAnnotation;
import com.cool.request.utils.PsiUtils;
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

public abstract class AnnotationControllerScan extends BaseControllerScan implements ControllerScan {
    private List<ControllerAnnotation> controllerAnnotations;

    public AnnotationControllerScan(ControllerConverter controllerConverter,
                                    List<ControllerAnnotation> controllerAnnotations) {
        super(controllerConverter);
        this.controllerAnnotations = controllerAnnotations;
    }

    @Override
    public List<Controller> scanController(Project project) {
        List<Controller> result = new ArrayList<>();
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        for (Module module : moduleManager.getModules()) {
            for (ControllerAnnotation controllerAnnotation : controllerAnnotations) {
                scanByAnnotation(project, module, result, controllerAnnotation);
            }
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
                    psiMethodToController(module.getProject(), originClass, module, psiMethod);
            if (controllers != null) result.addAll(controllers);
        }
        return result;
    }
}
