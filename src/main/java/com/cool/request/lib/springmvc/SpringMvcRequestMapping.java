package com.cool.request.lib.springmvc;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.lib.springmvc.param.*;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class SpringMvcRequestMapping {
    private final List<RequestParamSpeculate> requestParamSpeculates = new ArrayList<>();

    public SpringMvcRequestMapping() {
        requestParamSpeculates.add(new UrlParamSpeculate());
        requestParamSpeculates.add(new HeaderParamSpeculate());
        requestParamSpeculates.add(new BodyParamSpeculate());
        requestParamSpeculates.add(new FormDataSpeculate());
        requestParamSpeculates.add(new UrlencodedSpeculate());
        requestParamSpeculates.add(new PathParamSpeculate());
        requestParamSpeculates.add(new ResponseBodySpeculate());
    }

    public HttpRequestInfo getHttpRequestInfo(Project project, Controller controller) {
        if (controller instanceof CustomController) return new HttpRequestInfo();
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
        for (PsiMethod psiMethod : controller.getOwnerPsiMethod()) {
            for (RequestParamSpeculate requestParamSpeculate : requestParamSpeculates) {
                try {
                    requestParamSpeculate.set(psiMethod, httpRequestInfo);
                } catch (Exception e) {
                    //可能解析推测错误，主要是用户写得代码千奇百怪，防止万一
                }
            }
            return httpRequestInfo;
        }
        PsiClass psiClass = PsiUtils.findClassByName(project, controller.getModuleName(), controller.getSimpleClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = PsiUtils.findHttpMethodInClass(psiClass, controller);
            for (RequestParamSpeculate requestParamSpeculate : requestParamSpeculates) {
                requestParamSpeculate.set(methodInClass, httpRequestInfo);
            }
        }
        return httpRequestInfo;
    }
}
