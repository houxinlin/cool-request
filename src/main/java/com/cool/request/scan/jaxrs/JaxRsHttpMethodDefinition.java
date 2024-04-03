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

package com.cool.request.scan.jaxrs;

import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.scan.HttpMethodDefinition;
import com.cool.request.utils.UrlUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cool.request.lib.springmvc.utils.ParamUtils.getHttpUrlFromPsiAnnotationWithJaxRs;

public class JaxRsHttpMethodDefinition implements HttpMethodDefinition {
    @Override
    public List<HttpMethod> parseHttpMethod(PsiMethod psiMethod) {
        List<HttpMethod> httpMethods = new ArrayList<>();
        if (isGetRequest(psiMethod)) httpMethods.add(HttpMethod.GET);
        if (isPostRequest(psiMethod)) httpMethods.add(HttpMethod.POST);
        if (isDeleteRequest(psiMethod)) httpMethods.add(HttpMethod.DELETE);
        if (isPutRequest(psiMethod)) httpMethods.add(HttpMethod.PUT);
        if (isOptionRequest(psiMethod)) httpMethods.add(HttpMethod.OPTIONS);
        if (isHeadRequest(psiMethod)) httpMethods.add(HttpMethod.HEAD);
        if (isPatchRequest(psiMethod)) httpMethods.add(HttpMethod.PATCH);
        return httpMethods;
    }

    @Override
    public List<String> parseHttpUrl(PsiClass targetPsiClass, PsiMethod psiMethod) {
        PsiAnnotation requestMappingAnnotation = psiMethod.getAnnotation("javax.ws.rs.Path");
        if (requestMappingAnnotation != null) {
            return UrlUtils.mergeHttpUrl(getHttpUrl(targetPsiClass != null ? targetPsiClass : psiMethod.getContainingClass()),
                    getHttpUrlFromPsiAnnotationWithJaxRs(requestMappingAnnotation));
        }
        return new ArrayList<>();
    }

    public static boolean isOptionRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.OPTIONS");
    }

    public static boolean isGetRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.GET");
    }

    public static boolean isPostRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.POST");
    }

    public static boolean isHeadRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.HEAD");
    }

    public static boolean isPutRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.PUT");
    }

    public static boolean isPatchRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.PATCH");
    }

    public static boolean isDeleteRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.DELETE");
    }

    public static boolean isHttpRequestMethod(PsiMethod psiMethod, String mappingName) {
        if (mappingName != null) {
            PsiAnnotation getAnnotation = psiMethod.getAnnotation(mappingName);
            if (getAnnotation != null) return true;
        }
        return false;
    }

    private static List<String> getHttpUrl(PsiClass psiClass) {
        PsiAnnotation requestMappingAnnotation = psiClass.getAnnotation("javax.ws.rs.Path");
        if (requestMappingAnnotation != null) return getHttpUrlFromPsiAnnotationWithJaxRs(requestMappingAnnotation);

        for (PsiClass aSuper : psiClass.getSupers()) {
            requestMappingAnnotation = aSuper.getAnnotation("javax.ws.rs.Path");
            if (requestMappingAnnotation != null) return getHttpUrlFromPsiAnnotationWithJaxRs(requestMappingAnnotation);
        }
        return Collections.EMPTY_LIST;
    }
}
