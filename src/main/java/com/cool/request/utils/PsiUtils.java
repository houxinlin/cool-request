/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * PsiUtils.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.utils;

import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PsiUtils {
    public static Module findModuleByName(Project project, String moduleName) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getModules();
        for (Module module : modules) {
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    /**
     * 根据模块查找类
     */
    public static PsiClass findClassByName(Project project, Module module, String fullClassName) {
        fullClassName = ParamUtils.getStandardClassName(fullClassName);
        if (fullClassName == null) return null;
        if (module == null) return findClassByName(project, fullClassName);

        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(fullClassName, GlobalSearchScope.moduleScope(module));
        if (psiClass != null) return psiClass;
        return findClassByName(project, fullClassName);
    }

    /**
     * 在指定模块下查找，如果模块下不存在，则全局查找
     */
    public static PsiClass findClassByName(Project project, String moduleName, String fullClassName) {
        if (StringUtils.isEmpty(fullClassName)) return null;
        Module module = findModuleByName(project, moduleName);
        if (module == null) {
            return findClassByName(project, fullClassName);
        }
        return findClassByName(project, module, fullClassName);
    }

    /**
     * 全局查找类
     */
    public static PsiClass findClassByName(Project project, String fullClassName) {
        if (StringUtils.isEmpty(fullClassName)) return null;
        return JavaPsiFacade.getInstance(project).findClass(fullClassName, GlobalSearchScope.allScope(project));
    }


    /**
     * 查找指定方法名，如果不为空，则返回第一个
     */
    public static PsiMethod findMethodInClassOne(PsiClass psiClass, String methodName) {
        PsiMethod[] methodsByName = psiClass.findMethodsByName(methodName, true);
        if (methodsByName.length > 0) return methodsByName[0];
        return null;
    }

    private static String toStandard(String type) {
        if ("int[]".equalsIgnoreCase(type)) return "[I";
        if ("boolean[]".equalsIgnoreCase(type)) return "[Z";
        if ("byte[]".equalsIgnoreCase(type)) return "[B";
        if ("char[]".equalsIgnoreCase(type)) return "[C";
        if ("short[]".equalsIgnoreCase(type)) return "[S";
        if ("long[]".equalsIgnoreCase(type)) return "[J";
        if ("float[]".equalsIgnoreCase(type)) return "[F";
        if ("double[]".equalsIgnoreCase(type)) return "[D";
        if (type.contains("<") && type.endsWith("[]")) return toStandard(type.substring(0, type.indexOf("<")) + "[]");
        if (type.contains("<")) return toStandard(type.substring(0, type.indexOf("<")));
        if (type.endsWith("[]")) return "[L" + (type.replace("[]", "")) + ";";
        return type;
    }

    public static List<String> getParamClassList(PsiMethod psiMethod) {
        return Arrays.stream(psiMethod.getParameterList().getParameters())
                .map(psiParameter -> toStandard(psiParameter.getType().getCanonicalText())).collect(Collectors.toList());
    }

    public static PsiFile[] getUserProjectFile(String name, Project project, Module module) {
        if (module == null) return FilenameIndex.getFilesByName(project, name, GlobalSearchScope.allScope(project));
        return FilenameIndex.getFilesByName(project, name, GlobalSearchScope.moduleScope(module));

    }

    public static Module findClassNameModule(Project project, String className) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiClass[] classes = javaPsiFacade.findClasses(className, GlobalSearchScope.allScope(project));
        // TODO: 2024/1/10 用户或者三方库模块名称
        if (classes.length >= 1) {
            return ModuleUtil.findModuleForPsiElement(classes[0]);
        }
        return null;
    }

    public static boolean isAbstractClass(PsiClass psiClass) {
        return psiClass.hasModifierProperty(PsiModifier.ABSTRACT);
    }

    public static void methodNavigate(PsiMethod method) {
        if (method == null) return;
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
            method.navigate(false);
        });
    }

    public static boolean isObjectClass(PsiClass psiClass) {
        return "java.lang.Object".equals(psiClass.getQualifiedName());
    }

    public static String getPsiMethodClassName(PsiMethod psiMethod) {
        if (psiMethod.getContainingClass() != null) {
            return psiMethod.getContainingClass().getQualifiedName();
        }
        return "";
    }

    public static boolean hasExist(Project project, PsiClass psiClass) {
        String className = psiClass.getQualifiedName();
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project));
        return aClass != null;
    }
}
