package com.cool.request.component.convert;

import com.cool.request.common.bean.components.Component;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.StaticController;
import com.cool.request.component.ComponentConverter;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class DynamicControllerComponentConverter implements ComponentConverter<StaticController, DynamicController> {
    @Override
    public boolean canSupport(Component source, Component target) {
        return source instanceof StaticController && target instanceof DynamicController;
    }

    @Override
    public DynamicController converter(Project project, Component oldComponent, Component newComponent) {
        ((DynamicController) newComponent).setOwnerPsiMethod(((StaticController) oldComponent).getOwnerPsiMethod());
        if (((DynamicController) newComponent).getOwnerPsiMethod() == null || ((DynamicController) newComponent).getOwnerPsiMethod().isEmpty()) {
            ApplicationManager.getApplication().runReadAction(() -> {
                Module classNameModule = PsiUtils.findClassNameModule(project, ((DynamicController) newComponent).getJavaClassName());
                if (classNameModule != null) {
                    PsiClass psiClass = PsiUtils.findClassByName(classNameModule.getProject(), classNameModule, ((DynamicController) newComponent).getJavaClassName());
                    if (psiClass != null) {
                        PsiMethod httpMethodInClass = PsiUtils.findHttpMethodInClass(psiClass, ((DynamicController) newComponent));
                        if (httpMethodInClass != null) {
                            ((DynamicController) newComponent).setOwnerPsiMethod(List.of(httpMethodInClass));

                        }
                    }
                }
            });
        }
        return ((DynamicController) newComponent);
    }

}
