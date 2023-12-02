package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

public class PsiUtils {
    private static final Logger LOG = Logger.getInstance(PsiUtils.class);

    public static PsiClass findClassByName(Project project, String fullClassName) {
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(fullClassName, GlobalSearchScope.allScope(project));
        if (aClass != null) return aClass;
        String[] classNameParts = fullClassName.split("\\.");
        String className = classNameParts[classNameParts.length - 1];
        PsiClass[] items = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        for (PsiClass item : items) {
            String qualifiedName = item.getQualifiedName();
            if (qualifiedName.equals(fullClassName)) return item;
        }
        //不要删这个日志
        return null;
    }

    public static PsiMethod findMethod(Project project, String fullClassName, String methodName) {
        PsiClass classByName = findClassByName(project,fullClassName);
        if (classByName != null) return findMethodInClass(classByName, methodName);
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
