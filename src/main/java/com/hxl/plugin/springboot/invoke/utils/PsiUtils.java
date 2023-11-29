package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.SlowOperations;

public class PsiUtils {
    public static PsiClass findClassByName(Project project, String fullClassName) {
        String[] classNameParts = fullClassName.split("\\.");
        String className = classNameParts[classNameParts.length - 1];
        SlowOperations.assertSlowOperationsAreAllowed();
        PsiClass[] items = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        for (PsiClass item : items) {
            String qualifiedName = item.getQualifiedName();
            if (qualifiedName.equals(fullClassName)) return item;
        }
        return null;
    }

    public static PsiClass findClassByName(String fullClassName) {
        return findClassByName(ProjectManager.getInstance().getOpenProjects()[0], fullClassName);
    }
    public static PsiMethod findMethod(String fullClassName, String methodName) {
        PsiClass classByName = findClassByName(fullClassName);
        if (classByName!=null) return findMethodInClass(classByName,methodName);
        return null;
    }
    public static PsiMethod findMethodInClass(PsiClass psiClass, String methodName) {
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
