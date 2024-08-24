/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * JaxRsHttpMethodDefinition.java is part of Cool Request
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

package com.cool.request.scan.rose;

import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.scan.HttpMethodDefinition;
import com.cool.request.utils.UrlUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cool.request.lib.springmvc.utils.ParamUtils.getHttpUrlFromPsiAnnotationWithRose;

public class RoseHttpMethodDefinition implements HttpMethodDefinition {


    private static final String PATH = "net.paoding.rose.web.annotation.Path";
    private static final String OPTIONS = "net.paoding.rose.web.annotation.rest.Options";
    private static final String GET = "net.paoding.rose.web.annotation.rest.Get";
    private static final String POST = "net.paoding.rose.web.annotation.rest.Post";
    private static final String PUT = "net.paoding.rose.web.annotation.rest.Put";
    private static final String DELETE = "net.paoding.rose.web.annotation.rest.Delete";
    private static final String HEAD = "net.paoding.rose.web.annotation.rest.Head";
    private static final String TRACE = "net.paoding.rose.web.annotation.rest.Trace";

    @Override
    public List<HttpMethod> parseHttpMethod(PsiMethod psiMethod) {
        List<HttpMethod> httpMethods = new ArrayList<>();
        if (isGetRequest(psiMethod)) httpMethods.add(HttpMethod.GET);
        if (isPostRequest(psiMethod)) httpMethods.add(HttpMethod.POST);
        if (isPutRequest(psiMethod)) httpMethods.add(HttpMethod.PUT);
        if (isDeleteRequest(psiMethod)) httpMethods.add(HttpMethod.DELETE);
        if (isOptionRequest(psiMethod)) httpMethods.add(HttpMethod.OPTIONS);
        if (isHeadRequest(psiMethod)) httpMethods.add(HttpMethod.HEAD);
        if (isTraceRequest(psiMethod)) httpMethods.add(HttpMethod.TRACE);
        return httpMethods;
    }

    @Override
    public List<String> parseHttpUrl(PsiClass originClass, PsiMethod psiMethod) {
        if (isGetRequest(psiMethod))
            return getHttpUrl(GET, psiMethod, originClass);
        if (isPostRequest(psiMethod))
            return getHttpUrl(POST, psiMethod, originClass);
        if (isPutRequest(psiMethod))
            return getHttpUrl(PUT, psiMethod, originClass);
        if (isDeleteRequest(psiMethod))
            return getHttpUrl(DELETE, psiMethod, originClass);
        if (isOptionRequest(psiMethod)) {
            return getHttpUrl(OPTIONS, psiMethod, originClass);
        } else if (isHeadRequest(psiMethod)) {
            return getHttpUrl(HEAD, psiMethod, originClass);
        } else if (isTraceRequest(psiMethod)) {
            return getHttpUrl(TRACE, psiMethod, originClass);
        }
        return new ArrayList<>();
    }

    public static boolean isOptionRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, OPTIONS);
    }

    public static boolean isGetRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, GET);
    }

    public static boolean isPostRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, POST);
    }

    public static boolean isHeadRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, HEAD);
    }

    public static boolean isPutRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, PUT);
    }

    public static boolean isTraceRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, TRACE);
    }

    public static boolean isDeleteRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, DELETE);
    }

    public static boolean isHttpRequestMethod(PsiMethod psiMethod, String mappingName) {
        if (mappingName != null) {
            PsiAnnotation getAnnotation = psiMethod.getAnnotation(mappingName);
            if (getAnnotation != null) return true;
        }
        return false;
    }

    private static List<String> getHttpUrl(String mappingName, PsiMethod psiMethod, PsiClass originClass) {
        if (psiMethod == null) return new ArrayList<>();
        if (mappingName != null) {
            PsiAnnotation getAnnotation = psiMethod.getAnnotation(mappingName);
            if (getAnnotation != null) {
                PsiClass psiClass = originClass != null ? originClass : psiMethod.getContainingClass();
                PsiAnnotation pathAnnotation = psiClass.getAnnotation(PATH);
                return UrlUtils.mergeHttpUrl(getHttpUrlFromPsiAnnotationWithRose(pathAnnotation), ParamUtils.getHttpUrlFromPsiAnnotationWithRose(getAnnotation));
            }
        }
        return Collections.EMPTY_LIST;
    }
}
