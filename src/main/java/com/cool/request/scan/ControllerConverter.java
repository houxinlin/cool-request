package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.StaticController;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public interface ControllerConverter {
    public List<StaticController> psiMethodToController(PsiClass originClass , Module module, PsiMethod psiMethod);

    public PsiMethod controllerToPsiMethod(Project project, Controller controller);
}
