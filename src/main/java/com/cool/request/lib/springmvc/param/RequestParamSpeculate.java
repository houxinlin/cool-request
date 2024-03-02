package com.cool.request.lib.springmvc.param;

import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface RequestParamSpeculate {
    void set(PsiMethod method, HttpRequestInfo httpRequestInfo);

    default List<PsiParameter> listCanSpeculateParam(PsiMethod psiMethod) {
        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        return Arrays.stream(parameters)
                .filter(psiParameter ->
                        !(ParamUtils.isHttpServlet(psiParameter) || ParamUtils.isSpringBoot(psiParameter)))
                .collect(Collectors.toList());
    }
}
