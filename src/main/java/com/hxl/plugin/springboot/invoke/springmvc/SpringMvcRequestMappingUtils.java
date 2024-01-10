package com.hxl.plugin.springboot.invoke.springmvc;

import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.springmvc.param.*;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class SpringMvcRequestMappingUtils {
    private static final List<RequestParamSpeculate> requestParamSpeculates = new ArrayList<>();

    static {
        requestParamSpeculates.add(new UrlParamSpeculate());
        requestParamSpeculates.add(new HeaderParamSpeculate());
        requestParamSpeculates.add(new BodyParamSpeculate());
        requestParamSpeculates.add(new FormDataSpeculate());
        requestParamSpeculates.add(new UrlencodedSpeculate());
    }

    public static HttpRequestInfo getHttpRequestInfo(Project project, Controller controller) {
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo();

        PsiClass psiClass = PsiUtils.findClassByName(project,controller.getModuleName(), controller.getSimpleClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = PsiUtils.findHttpMethodInClass(psiClass,controller);
            for (RequestParamSpeculate requestParamSpeculate : requestParamSpeculates) {
                requestParamSpeculate.set(methodInClass, httpRequestInfo);
            }
        }
        return httpRequestInfo;
    }
}
