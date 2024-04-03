/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringMvcHttpMethodDefinition.java is part of Cool Request
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

package com.cool.request.scan.spring;

import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.lib.springmvc.config.reader.PropertiesReader;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.scan.HttpMethodDefinition;
import com.cool.request.utils.PropertyPlaceholderHelper;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.UrlUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpringMvcHttpMethodDefinition implements HttpMethodDefinition {
    @Override
    public List<HttpMethod> parseHttpMethod(PsiMethod psiMethod) {
        List<HttpMethod> httpMethods = new ArrayList<>();
        if (isGetRequest(psiMethod)) httpMethods.add(HttpMethod.GET);
        if (isPostRequest(psiMethod)) httpMethods.add(HttpMethod.POST);
        if (isDeleteRequest(psiMethod)) httpMethods.add(HttpMethod.DELETE);
        if (isPutRequest(psiMethod)) httpMethods.add(HttpMethod.PUT);
        if (isTraceRequest(psiMethod)) httpMethods.add(HttpMethod.TRACE);
        if (isOptionRequest(psiMethod)) httpMethods.add(HttpMethod.OPTIONS);
        if (isHeadRequest(psiMethod)) httpMethods.add(HttpMethod.HEAD);
        if (isPatchRequest(psiMethod)) httpMethods.add(HttpMethod.PATCH);
        return httpMethods;
    }

    public static boolean isNotGetRequest(PsiMethod psiMethod) {
        return !isGetRequest(psiMethod);
    }

    public static boolean isGetRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.GetMapping", "RequestMethod.GET");
    }

    public static boolean isPostRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.PostMapping", "RequestMethod.POST");
    }

    public static boolean isHeadRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, null, "RequestMethod.HEAD");
    }

    public static boolean isPutRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.PutMapping", "RequestMethod.PUT");
    }

    public static boolean isPatchRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.PatchMapping", "RequestMethod.PATCH");
    }

    public static boolean isDeleteRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.DeleteMapping", "RequestMethod.DELETE");
    }

    public static boolean isOptionRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, null, "RequestMethod.OPTIONS");
    }

    public static boolean isTraceRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, null, "RequestMethod.TRACE");
    }


    public static boolean isHttpRequestMethod(PsiMethod psiMethod, String mappingName, String httpMethod) {
        if (mappingName != null) {
            PsiAnnotation getAnnotation = psiMethod.getAnnotation(mappingName);
            if (getAnnotation != null) return true;
        }
        PsiAnnotation requestMappingAnnotation = psiMethod.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        if (requestMappingAnnotation != null) {
            String method = ParamUtils.getPsiAnnotationValues(requestMappingAnnotation).get("method");
            if (method == null) return true;
            return httpMethod.equalsIgnoreCase(method);
        }
        return false;
    }

    public static boolean hasHttpMethod(PsiMethod psiMethod) {
        if (isGetRequest(psiMethod)) return true;
        if (isPutRequest(psiMethod)) return true;
        if (isPostRequest(psiMethod)) return true;
        if (isDeleteRequest(psiMethod)) return true;
        if (isTraceRequest(psiMethod)) return true;
        if (isOptionRequest(psiMethod)) return true;
        if (isHeadRequest(psiMethod)) return true;
        if (isPatchRequest(psiMethod)) return true;
        return false;
    }

    public List<String> getHttpUrl(PsiMethod psiMethod) {
        return parseHttpUrl(psiMethod.getContainingClass(), psiMethod);
//        List<String> superUrl = new ArrayList<>();
//        PsiMethod[] superMethods = psiMethod.findSuperMethods(false);
//        for (PsiMethod superMethod : superMethods) {
//            if (superMethod.getContainingClass() != null) {
//                List<String> url = getHttpUrl(superMethod.getContainingClass(), superMethod);
//                if (url != null) {
//                    superUrl.addAll(url);
//                }
//            }
//        }
//        return CollectionUtils.merge(httpUrl, superUrl);
    }

    @Override
    public List<String> parseHttpUrl(PsiClass originClass, PsiMethod psiMethod) {
        if (isGetRequest(psiMethod))
            return getHttpUrl("org.springframework.web.bind.annotation.GetMapping", psiMethod, originClass);
        if (isPutRequest(psiMethod))
            return getHttpUrl("org.springframework.web.bind.annotation.PutMapping", psiMethod, originClass);
        if (isPostRequest(psiMethod))
            return getHttpUrl("org.springframework.web.bind.annotation.PostMapping", psiMethod, originClass);
        if (isDeleteRequest(psiMethod))
            return getHttpUrl("org.springframework.web.bind.annotation.DeleteMapping", psiMethod, originClass);
        if (isPatchRequest(psiMethod))
            return getHttpUrl("org.springframework.web.bind.annotation.PatchMapping", psiMethod, originClass);
        if (isOptionRequest(psiMethod) || isHeadRequest(psiMethod) || isTraceRequest(psiMethod))
            return getHttpUrl(null, psiMethod, originClass);
        return new ArrayList<>();
    }

    private static List<String> getHttpUrl(String mappingName, PsiMethod psiMethod, PsiClass originClass) {
        if (psiMethod == null) return new ArrayList<>();
        if (mappingName != null) {
            PsiAnnotation getAnnotation = psiMethod.getAnnotation(mappingName);
            if (getAnnotation != null) {
                return replacePlaceholders(UrlUtils.mergeHttpUrl(getHttpUrlFromClassRequestMapping(originClass != null ? originClass : psiMethod.getContainingClass()),
                        ParamUtils.getHttpUrlFromPsiAnnotationWithSpringMvc(getAnnotation)), ModuleUtil.findModuleForPsiElement(psiMethod));
            }
        }
        PsiAnnotation requestMappingAnnotation = psiMethod.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        if (requestMappingAnnotation != null) {
            return replacePlaceholders(UrlUtils.mergeHttpUrl(getHttpUrlFromClassRequestMapping(originClass != null ? originClass : psiMethod.getContainingClass()),
                    ParamUtils.getHttpUrlFromPsiAnnotationWithSpringMvc(requestMappingAnnotation)), ModuleUtil.findModuleForPsiElement(psiMethod));
        }
        return Collections.EMPTY_LIST;

    }

    private static List<String> replacePlaceholders(List<String> value, Module module) {
        List<String> result = new ArrayList<>();
        PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
        for (String s : value) {
            result.add(propertyPlaceholderHelper.replacePlaceholders(s, placeholderName -> {
                String s1 = new PropertiesReader().readCustomAsString(placeholderName, module.getProject(), module);
                return s1;
            }));
        }
        return result;
    }

    private static List<String> getHttpUrlFromClassRequestMapping(PsiClass psiClass) {
        if (psiClass == null) return new ArrayList<>();
        PsiAnnotation requestMappingAnnotation = psiClass.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        if (requestMappingAnnotation != null)
            return ParamUtils.getHttpUrlFromPsiAnnotationWithSpringMvc(requestMappingAnnotation);

        for (PsiClass aSuper : psiClass.getSupers()) {
            requestMappingAnnotation = aSuper.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
            if (requestMappingAnnotation != null)
                return ParamUtils.getHttpUrlFromPsiAnnotationWithSpringMvc(requestMappingAnnotation);
        }
        return new ArrayList<>();
    }

}
