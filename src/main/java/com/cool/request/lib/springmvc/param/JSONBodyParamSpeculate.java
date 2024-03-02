package com.cool.request.lib.springmvc.param;

import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.List;

public class JSONBodyParamSpeculate extends BasicBodySpeculate implements RequestParamSpeculate {
    public JSONBodyParamSpeculate() {
    }

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        List<PsiParameter> parameters = listCanSpeculateParam(method);
        //非GET和具有表单的请求不设置此选项
        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(parameters)) {
            //没有RequestBody注解，不要设置任何JSON参数
            PsiParameter requestBodyPsiParameter = ParamUtils.getParametersWithAnnotation(method, "org.springframework.web.bind.annotation.RequestBody");
            if (requestBodyPsiParameter != null) {
                PsiClass psiClass = PsiUtils.findClassByName(method.getProject(),
                        ModuleUtil.findModuleForPsiElement(method).getName(),
                        requestBodyPsiParameter.getType().getCanonicalText());
                if (psiClass != null) setJSONRequestBody(psiClass, httpRequestInfo);
            }
        }
    }

}
