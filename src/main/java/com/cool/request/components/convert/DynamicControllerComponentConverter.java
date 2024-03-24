package com.cool.request.components.convert;

import com.cool.request.common.bean.components.Component;
import com.cool.request.components.ComponentConverter;
import com.cool.request.components.http.DynamicController;
import com.cool.request.components.http.StaticController;
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
        if (source == null && target instanceof DynamicController) return true;
        return (source instanceof StaticController && target instanceof DynamicController) ||
                (source instanceof DynamicController && target instanceof DynamicController);
    }

    @Override
    public DynamicController converter(Project project, Component source, Component target) {
        if ((source instanceof StaticController && target instanceof DynamicController)) {
            ((DynamicController) target).setOwnerPsiMethod(((StaticController) source).getOwnerPsiMethod());
        }

        if (((DynamicController) target).getOwnerPsiMethod() == null || ((DynamicController) target).getOwnerPsiMethod().isEmpty()) {
            ApplicationManager.getApplication().runReadAction(() -> {
                Module classNameModule = PsiUtils.findClassNameModule(project, ((DynamicController) target).getJavaClassName());
                if (classNameModule != null) {
                    PsiClass psiClass = PsiUtils.findClassByName(classNameModule.getProject(), classNameModule, ((DynamicController) target).getJavaClassName());
                    if (psiClass != null) {
                        PsiMethod httpMethodInClass = PsiUtils.findHttpMethodInClass(psiClass, ((DynamicController) target));
                        if (httpMethodInClass != null) {
                            ((DynamicController) target).setOwnerPsiMethod(List.of(httpMethodInClass));

                        }
                    }
                }
            });
        }
        return ((DynamicController) target);
    }

}
