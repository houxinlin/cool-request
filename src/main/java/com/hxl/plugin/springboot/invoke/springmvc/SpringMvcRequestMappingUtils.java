package com.hxl.plugin.springboot.invoke.springmvc;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.hxl.plugin.springboot.invoke.springmvc.param.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class SpringMvcRequestMappingUtils {
    private static final List<RequestParamSpeculate> requestParamSpeculates = new ArrayList<>();

    static {
        requestParamSpeculates.add(new UrlParamSpeculate());
        requestParamSpeculates.add(new HeaderParamSpeculate());
        requestParamSpeculates.add(new JsonBodyParamSpeculate());
        requestParamSpeculates.add(new FormDataSpeculate());
        requestParamSpeculates.add(new UrlencodedSpeculate());
    }
    public static HttpRequestInfo getHttpRequestInfo(RequestMappingModel requestMappingModel){
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];
        PsiClass psiClass = PsiUtils.findClassByName(openProject, requestMappingModel.getController().getSimpleClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = PsiUtils.findMethodInClass(psiClass, requestMappingModel.getController().getMethodName());
            for (RequestParamSpeculate requestParamSpeculate : requestParamSpeculates) {
                requestParamSpeculate.set(methodInClass,  httpRequestInfo);
            }
        }
        return httpRequestInfo;
    }
}
