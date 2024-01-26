package com.cool.request.component.api.scans;

import com.cool.request.common.bean.components.scheduled.SpringScheduled;
import com.cool.request.lib.springmvc.ScheduledAnnotation;
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
import java.util.List;
import java.util.Objects;

public class SpringScheduledScan {
    public List<SpringScheduled> scan(Project project) {
        List<SpringScheduled> result = new ArrayList<>();
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        for (Module module : moduleManager.getModules()) {
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(ScheduledAnnotation.SCHEDULED_ANNOTATION.getName(), project,
                    GlobalSearchScope.moduleScope(module));

            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiElement psiAnnotationParent = psiAnnotation.getParent();
                if (!ScheduledAnnotation.SCHEDULED_ANNOTATION.getAnnotationName().equalsIgnoreCase(psiAnnotation.getQualifiedName())) {
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
                SpringScheduled springScheduled = SpringScheduled.SpringScheduledBuilder.aSpringScheduled()
                        .withModuleName(module.getName())
                        .withServerPort(-1)
                        .withMethodName(psiMethod.getName())
                        .withSimpleClassName(Objects.requireNonNull(psiMethod.getContainingClass()).getQualifiedName())
                        .build(project, new SpringScheduled());
                result.add(springScheduled);
            }
        }
        return result;
    }
}
