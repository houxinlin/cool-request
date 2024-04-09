/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringScheduledScan.java is part of Cool Request
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

import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.components.scheduled.SpringScheduled;
import com.cool.request.components.scheduled.XxlJobScheduled;
import com.cool.request.lib.springmvc.ScheduledAnnotation;
import com.cool.request.scan.ScheduledScan;
import com.cool.request.utils.ComponentIdUtils;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SpringScheduledScan implements ScheduledScan {
    private BasicScheduled createScheduledInstance(ScheduledAnnotation scheduledAnnotation) {
        if (scheduledAnnotation == ScheduledAnnotation.SCHEDULED_ANNOTATION) return new SpringScheduled();
        if (scheduledAnnotation == ScheduledAnnotation.XXL_JOB_ANNOTATION) return new XxlJobScheduled();

        return null;

    }

    @Override
    public List<BasicScheduled> scanScheduled(Project project) {
        List<BasicScheduled> result = new ArrayList<>();
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        for (Module module : moduleManager.getModules()) {
            for (ScheduledAnnotation value : ScheduledAnnotation.values()) {
                Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance()
                        .get(value.getName(), project, GlobalSearchScope.moduleScope(module));

                for (PsiAnnotation psiAnnotation : psiAnnotations) {
                    PsiElement psiAnnotationParent = psiAnnotation.getParent();
                    if (!value.getAnnotationName().equalsIgnoreCase(psiAnnotation.getQualifiedName())) {
                        continue;
                    }
                    if (psiAnnotationParent == null) {
                        continue;
                    }
                    if (!(psiAnnotationParent instanceof PsiModifierList)) {
                        continue;
                    }
                    PsiElement psiElement = psiAnnotationParent.getParent();
                    if (!(psiElement instanceof PsiMethod)) {
                        continue;
                    }
                    PsiMethod psiMethod = (PsiMethod) psiElement;
                    BasicScheduled scheduled = createScheduledInstance(value);
                    if (scheduled != null) {
                        scheduled.setModuleName(module.getName());
                        scheduled.setServerPort(-1);
                        scheduled.setMethodName(psiMethod.getName());
                        scheduled.setClassName(PsiUtils.getPsiMethodClassName(psiMethod));
                        scheduled.setId(ComponentIdUtils.getMd5(project, scheduled));
                    }
                    result.add(scheduled);
                }
            }
        }
        result.sort(Comparator.comparing(BasicScheduled::getClassName));
        return result;
    }
}
