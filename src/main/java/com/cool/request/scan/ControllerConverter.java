package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.StaticController;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiMethod;

import java.util.List;

public interface ControllerConverter {
    public List<StaticController> psiMethodToController(Module module,PsiMethod psiMethod, String context, int port);

    public PsiMethod controllerToPsiMethod(Controller controller);
}
