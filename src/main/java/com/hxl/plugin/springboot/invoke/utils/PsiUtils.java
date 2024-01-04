package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

import java.util.*;
import java.util.stream.Collectors;

public class PsiUtils {
    private static final Logger LOG = Logger.getInstance(PsiUtils.class);

    public static PsiClass findClassByName(Project project, String fullClassName) {
        fullClassName = ParamUtils.getStandardClassName(fullClassName);
        if (fullClassName == null) return null;
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(fullClassName, GlobalSearchScope.allScope(project));
        if (aClass != null) return aClass;
        String[] classNameParts = fullClassName.split("\\.");
        String className = classNameParts[classNameParts.length - 1];
        PsiClass[] items = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        for (PsiClass item : items) {
            String qualifiedName = item.getQualifiedName();
            if (Optional.ofNullable(qualifiedName).orElse("").equals(fullClassName)) return item;
        }
        //不要删这个日志
        return null;
    }

    //    public static List<PsiMethod> findMethod(Project project, String fullClassName, String methodName) {
//        PsiClass classByName = findClassByName(project,fullClassName);
//        if (classByName != null) return findMethodInClass(classByName, methodName);
//        return null;
//    }
    public static List<PsiMethod> findMethod(Project project, String fullClassName, String methodName) {
        PsiClass classByName = findClassByName(project, fullClassName);
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

    public static PsiMethod findHttpMethodInClass(Project project,
                                                  SpringMvcRequestMappingSpringInvokeEndpoint springMvcRequestMappingSpringInvokeEndpoint) {
        PsiClass psiClass = findClassByName(project, springMvcRequestMappingSpringInvokeEndpoint.getSimpleClassName());
        if (psiClass == null) return null;
        return findHttpMethodInClass(psiClass,
                springMvcRequestMappingSpringInvokeEndpoint.getMethodName(),
                springMvcRequestMappingSpringInvokeEndpoint.getHttpMethod(),
                springMvcRequestMappingSpringInvokeEndpoint.getParamClassList(), springMvcRequestMappingSpringInvokeEndpoint.getUrl());
    }

    public static PsiMethod findHttpMethodInClass(PsiClass psiClass,
                                                  SpringMvcRequestMappingSpringInvokeEndpoint springMvcRequestMappingSpringInvokeEndpoint) {
        return findHttpMethodInClass(psiClass,
                springMvcRequestMappingSpringInvokeEndpoint.getMethodName(),
                springMvcRequestMappingSpringInvokeEndpoint.getHttpMethod(),
                springMvcRequestMappingSpringInvokeEndpoint.getParamClassList(),
                springMvcRequestMappingSpringInvokeEndpoint.getUrl());

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
            String[] httpUrl = ParamUtils.getHttpUrl(psiMethod);
            for (String urlItem : Optional.ofNullable(httpUrl).orElse(new String[]{})) {
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
}
