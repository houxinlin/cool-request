/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * PsiJaxRsUtils.java is part of Cool Request
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

import com.cool.request.components.http.net.HttpMethod;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cool.request.lib.springmvc.utils.ParamUtils.*;

/**
 * @author zhangpengjun
 * @date 2024/3/5
 */
public class PsiJaxRsUtils extends PsiUtils {

    public static List<HttpMethod> getHttpMethod(PsiMethod psiMethod) {
        List<HttpMethod> httpMethods = new ArrayList<>();
        if (isGetRequest(psiMethod)) httpMethods.add(HttpMethod.GET);
        if (isPostRequest(psiMethod)) httpMethods.add(HttpMethod.POST);
        if (isDeleteRequest(psiMethod)) httpMethods.add(HttpMethod.DELETE);
        if (isPutRequest(psiMethod)) httpMethods.add(HttpMethod.PUT);
        if (isTraceRequest(psiMethod)) httpMethods.add(HttpMethod.TRACE);
        if (isOptionRequest(psiMethod)) httpMethods.add(HttpMethod.OPTIONS);
        if (isHeadRequest(psiMethod)) httpMethods.add(HttpMethod.HEAD);
        return httpMethods;
    }

    public static boolean isGetRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.GET", null);
    }

    public static boolean isPostRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.POST", null);
    }

    public static boolean isHeadRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.HEAD", null);
    }

    public static boolean isPutRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.PUT", null);
    }

    public static boolean isPatchRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.PATCH", null);
    }

    public static boolean isDeleteRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "javax.ws.rs.DELETE", null);
    }

    public static List<String> getHttpUrl(PsiClass targetPsiClass, PsiMethod psiMethod) {
        if (isGetRequest(psiMethod))
            return getHttpUrl("javax.ws.rs.GET", psiMethod, targetPsiClass);
        if (isPutRequest(psiMethod))
            return getHttpUrl("javax.ws.rs.PUT", psiMethod, targetPsiClass);
        if (isPostRequest(psiMethod))
            return getHttpUrl("javax.ws.rs.POST", psiMethod, targetPsiClass);
        if (isDeleteRequest(psiMethod))
            return getHttpUrl("javax.ws.rs.DELETE", psiMethod, targetPsiClass);
        if (isPatchRequest(psiMethod))
            return getHttpUrl("javax.ws.rs.PATCH", psiMethod, targetPsiClass);

        if (isOptionRequest(psiMethod) || isHeadRequest(psiMethod) || isTraceRequest(psiMethod))
            return getHttpUrl(null, psiMethod, targetPsiClass);
        return null;
    }

    private static List<String> getHttpUrl(String mappingName, PsiMethod psiMethod, PsiClass targetPsiClass) {
        if (psiMethod == null) return Collections.EMPTY_LIST;
        if (mappingName != null) {
            PsiAnnotation getAnnotation = psiMethod.getAnnotation(mappingName);
            if (getAnnotation != null) {
                return mergeHttpUrl(getHttpUrl(targetPsiClass != null ? targetPsiClass : psiMethod.getContainingClass()),
                        getHttpUrlFromPsiAnnotation(getAnnotation));
            }
        }
        PsiAnnotation requestMappingAnnotation = psiMethod.getAnnotation("javax.ws.rs.Path");
        if (requestMappingAnnotation != null) {
            return mergeHttpUrl(getHttpUrl(targetPsiClass != null ? targetPsiClass : psiMethod.getContainingClass()),
                    getHttpUrlFromPsiAnnotation(requestMappingAnnotation));
        }
        return Collections.EMPTY_LIST;

    }

    private static List<String> getHttpUrl(PsiClass psiClass) {
        PsiAnnotation requestMappingAnnotation = psiClass.getAnnotation("javax.ws.rs.Path");
        if (requestMappingAnnotation != null) return getHttpUrlFromPsiAnnotation(requestMappingAnnotation);

        for (PsiClass aSuper : psiClass.getSupers()) {
            requestMappingAnnotation = aSuper.getAnnotation("javax.ws.rs.Path");
            if (requestMappingAnnotation != null) return getHttpUrlFromPsiAnnotation(requestMappingAnnotation);
        }
        return Collections.EMPTY_LIST;
    }

}
