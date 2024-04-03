package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public interface ControllerConverter {
    public List<HttpMethod> parseHttpMethod(PsiMethod psiMethod);

    public default boolean canConverter(PsiMethod psiMethod) {
        return false;
    }

    public List<StaticController> psiMethodToController(Project project, PsiClass originClass, Module module, PsiMethod psiMethod);

    public default PsiMethod controllerToPsiMethod(Project project, Controller controller) {
        PsiClass psiClass = PsiUtils.findClassByName(project, controller.getModuleName(), controller.getSimpleClassName());
        if (psiClass != null) {
            PsiMethod[] methodsByName = psiClass.findMethodsByName(controller.getMethodName(), true);
            boolean canDefault = false;
            for (PsiMethod psiMethod : methodsByName) {
                List<HttpMethod> httpMethods = parseHttpMethod(psiMethod);
                if (httpMethods.isEmpty()) continue;
                canDefault = true;
                Module module = ModuleUtil.findModuleForPsiElement(psiMethod);
                List<StaticController> staticControllers = psiMethodToController(project, psiClass, module, psiMethod);
                for (StaticController staticController : staticControllers) {
                    if (StringUtils.isEqualsIgnoreCase(staticController.getId(), controller.getId())) {
                        return psiMethod;
                    }
                }
            }
            if (canDefault) {
                return methodsByName[0];
            }
            return null;
        }
        return null;
    }
}
