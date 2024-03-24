package com.cool.request.utils;

import com.cool.request.components.JavaClassComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class ComponentUtils {
    public static <T extends JavaClassComponent> void init(Project project, T component) {
        if (!StringUtils.isEmpty(component.getModuleName())) return;
        ApplicationManager.getApplication().runReadAction(() -> {
            Module classNameModule = PsiUtils.findClassNameModule(project, component.getJavaClassName());
            component.setModuleName(classNameModule == null ? "unknown" : classNameModule.getName());
        });
    }
}
