package com.hxl.plugin.springboot.invoke.scans.controller;

import com.hxl.plugin.springboot.invoke.bean.StaticRequestMappingWrapper;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.bean.components.controller.DynamicController;
import com.hxl.plugin.springboot.invoke.bean.components.controller.StaticController;
import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.springmvc.ControllerAnnotation;
import com.hxl.plugin.springboot.invoke.springmvc.config.reader.UserProjectContextPathReader;
import com.hxl.plugin.springboot.invoke.springmvc.config.reader.UserProjectServerPortReader;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpringMvcControllerScan {

    public List<Controller> scan(Project project) {
        List<Controller> result = new ArrayList<>();
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        for (Module module : moduleManager.getModules()) {
            UserProjectServerPortReader userProjectServerPortReader = new UserProjectServerPortReader(project, module);
            UserProjectContextPathReader userProjectContextPathReader = new UserProjectContextPathReader(project, module);

            Integer currentModuleServerPort = userProjectServerPortReader.read();
            String contextPath = userProjectContextPathReader.read();

            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(ControllerAnnotation.RestController.getName(), project,
                    GlobalSearchScope.moduleScope(module));

            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiElement psiAnnotationParent = psiAnnotation.getParent();
                if (psiAnnotationParent == null) continue;
                if (!(psiAnnotationParent instanceof PsiModifierList)) continue;
                PsiElement psiElement = psiAnnotationParent.getParent();
                if (!(psiElement instanceof PsiClass)) {
                    continue;
                }
                result.addAll(extractHttpRouteMethods((PsiClass) psiElement, module,currentModuleServerPort, contextPath));
            }


        }
        return result;
    }

    private Collection<StaticController> extractHttpRouteMethods(PsiClass psiElement,
                                                                 Module module,
                                                                 Integer currentModuleServerPort,
                                                                 String contextPath) {
        List<StaticController> result = new ArrayList<>();
        for (PsiMethod psiMethod : psiElement.getAllMethods()) {
            List<HttpMethod> httpMethod = PsiUtils.getHttpMethod(psiMethod);
            if (httpMethod.isEmpty()) continue;
            String[] httpUrl = ParamUtils.getHttpUrl(psiMethod);
            if (httpUrl == null) continue;

            StaticController controller =(StaticController) Controller.ControllerBuilder.aController()
                    .withHttpMethod(httpMethod.get(0).toString())
                    .withMethodName(psiMethod.getName())
                    .withContextPath(contextPath)
                    .withServerPort(currentModuleServerPort)
                    .withModuleName(module.getName())
                    .withUrl(httpUrl[0]) //这里有问题，先获取第一个
                    .withSimpleClassName(psiMethod.getContainingClass().getQualifiedName())
                    .withParamClassList(PsiUtils.getParamClassList(psiMethod))
                    .build(new StaticController());
            result.add(controller);
        }
        return result;
    }

}
