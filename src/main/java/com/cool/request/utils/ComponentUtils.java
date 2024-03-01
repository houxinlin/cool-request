package com.cool.request.utils;

import com.cool.request.component.JavaClassComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class ComponentUtils {
    public static <T extends JavaClassComponent> void init(Project project, T component) {
        ApplicationManager.getApplication().runReadAction(() -> {
            if (!StringUtils.isEmpty(component.getModuleName())) return;
            Module classNameModule = PsiUtils.findClassNameModule(project, component.getJavaClassName());
            component.setModuleName(classNameModule == null ? "unknown" : classNameModule.getName());
        });
    }
}
