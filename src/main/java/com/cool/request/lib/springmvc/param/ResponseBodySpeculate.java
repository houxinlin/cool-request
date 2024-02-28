package com.cool.request.lib.springmvc.param;

import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

public class ResponseBodySpeculate extends BasicBodySpeculate implements RequestParamSpeculate {

    public ResponseBodySpeculate() {
    }

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        PsiType returnType = method.getReturnType();
        if (returnType instanceof PsiClassType) {
            PsiClass resolve = ((PsiClassType) returnType).resolve();
            if (resolve != null) {
                setResponseBody(resolve, httpRequestInfo);
            }
        }
    }

}
