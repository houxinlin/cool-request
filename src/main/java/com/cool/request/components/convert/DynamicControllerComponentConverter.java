/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DynamicControllerComponentConverter.java is part of Cool Request
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

package com.cool.request.components.convert;

import com.cool.request.common.bean.components.Component;
import com.cool.request.components.ComponentConverter;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.DynamicController;
import com.cool.request.components.http.StaticController;
import com.cool.request.utils.ControllerUtils;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class DynamicControllerComponentConverter implements ComponentConverter<StaticController, DynamicController> {
    private Project project;

    public DynamicControllerComponentConverter(Project project) {
        this.project = project;
    }

    @Override
    public boolean canSupport(Component source, Component target) {
        if (source == null && target instanceof DynamicController) return true;
        return (source instanceof StaticController && target instanceof DynamicController) ||
                (source instanceof DynamicController && target instanceof DynamicController);
    }

    @Override
    public DynamicController converter(Project project, Component source, Component target) {
        ControllerUtils.copy(((Controller) source), ((Controller) target));

        if (((DynamicController) target).getOwnerPsiMethod() == null || ((DynamicController) target).getOwnerPsiMethod().isEmpty() ||
                ((DynamicController) target).getParamClassList() == null || ((DynamicController) target).getParamClassList().isEmpty()) {
            ApplicationManager.getApplication().runReadAction(() -> {
                Module classNameModule = PsiUtils.findClassNameModule(project, ((DynamicController) target).getJavaClassName());
                PsiClass psiClass = null;
                if (classNameModule != null) {
                    psiClass = PsiUtils.findClassByName(classNameModule.getProject(), classNameModule, ((DynamicController) target).getJavaClassName());
                }
                if (psiClass == null) {
                    psiClass = PsiUtils.findClassByName(project, ((DynamicController) target).getJavaClassName());
                }
                if (psiClass != null) {
                    if (psiClass != null) {
                        PsiMethod httpMethodInClass = PsiUtils.findHttpMethodInClass(psiClass, ((DynamicController) target));
                        if (((DynamicController) target).getOwnerPsiMethod() == null) {
                            ((DynamicController) target).setOwnerPsiMethod(List.of(httpMethodInClass));
                        }
                        if (((DynamicController) target).getParamClassList() == null) {
                            ((DynamicController) target).setParamClassList(PsiUtils.getParamClassList(httpMethodInClass));
                        }
                    }
                }
            });
        }
        if (((Controller) target).getOwnerPsiMethod() == null) {
            ((Controller) target).setOwnerPsiMethod(new ArrayList<>());
        }
        return ((DynamicController) target);
    }

}
