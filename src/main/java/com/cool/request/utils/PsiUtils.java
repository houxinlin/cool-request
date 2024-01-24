package com.cool.request.utils;

import com.cool.request.bean.components.controller.Controller;
import com.cool.request.net.HttpMethod;
import com.cool.request.springmvc.utils.ParamUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PsiUtils {
    private static final Logger LOG = Logger.getInstance(PsiUtils.class);

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

    public static PsiClass findClassByName(Project project, Module module, String fullClassName) {
        fullClassName = ParamUtils.getStandardClassName(fullClassName);
        if (fullClassName == null) return null;
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(fullClassName, GlobalSearchScope.moduleScope(module));
        if (aClass != null) return aClass;
        String[] classNameParts = fullClassName.split("\\.");
        String className = classNameParts[classNameParts.length - 1];
        PsiClass[] items = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        for (PsiClass item : items) {
            String qualifiedName = item.getQualifiedName();
            if (Optional.ofNullable(qualifiedName).orElse("").equals(fullClassName)) return item;
        }
        return null;
    }

    public static PsiClass findClassByName(Project project, String moduleName, String fullClassName) {
        return findClassByName(project, findModuleByName(project, moduleName), fullClassName);
    }

    public static List<PsiMethod> findMethod(Project project, Module module, String fullClassName, String methodName) {
        PsiClass classByName = findClassByName(project, module, fullClassName);
        if (classByName != null) return findMethodInClass(classByName, methodName);
        return null;
    }


    public static List<PsiMethod> findMethodInClass(PsiClass psiClass, String methodName) {
        List<PsiMethod> list = new ArrayList<>();
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (method.getName().equals(methodName)) {
                list.add(method);
            }
        }
        return list;
    }

    public static PsiMethod findMethodInClassOne(PsiClass psiClass, String methodName) {
        List<PsiMethod> list = findMethodInClass(psiClass, methodName);
        if (!list.isEmpty()) return list.get(0);
        return null;
    }

    public static PsiMethod findHttpMethodInClass(PsiClass psiClass,
                                                  Controller controller) {
        return findHttpMethodInClass(psiClass,
                controller.getMethodName(),
                controller.getHttpMethod(),
                controller.getParamClassList(),
                controller.getUrl());

    }

    public static PsiMethod findHttpMethodInClass(PsiClass psiClass,
                                                  String methodName,
                                                  String httpMethod,
                                                  List<String> paramClassList, String url) {
        List<PsiMethod> methodInClass = findMethodInClass(psiClass, methodName);
        //精准匹配
        for (PsiMethod psiMethod : methodInClass) {
            if (!ParamUtils.isEquals(paramClassList, PsiUtils.getParamClassList(psiMethod))) continue;
            if (httpMethod.equalsIgnoreCase("get") && ParamUtils.isGetRequest(psiMethod)) return psiMethod;
            if (httpMethod.equalsIgnoreCase("put") && ParamUtils.isPutRequest(psiMethod)) return psiMethod;
            if (httpMethod.equalsIgnoreCase("post") && ParamUtils.isPostRequest(psiMethod)) return psiMethod;
            if (httpMethod.equalsIgnoreCase("delete") && ParamUtils.isDeleteRequest(psiMethod)) return psiMethod;
            if (httpMethod.equalsIgnoreCase("trace") && ParamUtils.isTraceRequest(psiMethod)) return psiMethod;
            if (httpMethod.equalsIgnoreCase("option") && ParamUtils.isOptionRequest(psiMethod)) return psiMethod;
            if (httpMethod.equalsIgnoreCase("head") && ParamUtils.isHeadRequest(psiMethod)) return psiMethod;
            if (httpMethod.equalsIgnoreCase("patch") && ParamUtils.isPatchRequest(psiMethod)) return psiMethod;
        }
        //模糊匹配
        int max = -1;
        PsiMethod result = null;
        if (methodInClass.isEmpty()) {
            for (PsiMethod item : psiClass.getAllMethods()) {
                if (ParamUtils.hasHttpMethod(item)) methodInClass.add(item);
            }
        }
        for (PsiMethod psiMethod : methodInClass) {
            List<String> httpUrl = ParamUtils.getHttpUrl(psiMethod);
            for (String urlItem : Optional.ofNullable(httpUrl).orElse(new ArrayList<>())) {
                List<HttpMethod> supportMethod = getHttpMethod(psiMethod);
                if (url.endsWith(urlItem) && urlItem.length() > max && ParamUtils.httpMethodIn(supportMethod, HttpMethod.parse(httpMethod))) {
                    result = psiMethod;
                    max = urlItem.length();
                }
            }
        }
        return result;
    }

    public static List<HttpMethod> getHttpMethod(PsiMethod psiMethod) {
        List<HttpMethod> httpMethods = new ArrayList<>();
        if (ParamUtils.isGetRequest(psiMethod)) httpMethods.add(HttpMethod.GET);
        if (ParamUtils.isPostRequest(psiMethod)) httpMethods.add(HttpMethod.POST);
        if (ParamUtils.isDeleteRequest(psiMethod)) httpMethods.add(HttpMethod.DELETE);
        if (ParamUtils.isPutRequest(psiMethod)) httpMethods.add(HttpMethod.PUT);
        if (ParamUtils.isTraceRequest(psiMethod)) httpMethods.add(HttpMethod.TRACE);
        if (ParamUtils.isOptionRequest(psiMethod)) httpMethods.add(HttpMethod.OPTIONS);
        if (ParamUtils.isHeadRequest(psiMethod)) httpMethods.add(HttpMethod.HEAD);
        return httpMethods;

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
        return FilenameIndex.getFilesByName(project, name, GlobalSearchScope.moduleScope(module));

    }

    public static Module findClassNameModule(Project project, String className) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiClass[] classes = javaPsiFacade.findClasses(className, GlobalSearchScope.allScope(project));
        // TODO: 2024/1/10 用户或者三方库模块名称
        if (classes != null && classes.length >= 1) {
            Module module = ModuleUtil.findModuleForPsiElement(classes[0]);
            return module;
        }
        return null;
    }

    public static boolean isAbstractClass(PsiClass psiClass) {
        return psiClass.hasModifierProperty(PsiModifier.ABSTRACT);
    }

    public static void methodNavigate(PsiMethod method) {
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
            method.navigate(true);
        });
    }

    public static boolean isObjectClass(PsiClass psiClass) {
        return "java.lang.Object".equals(psiClass.getQualifiedName());
    }
}
