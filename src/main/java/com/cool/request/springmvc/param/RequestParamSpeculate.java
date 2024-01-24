package com.cool.request.springmvc.param;

import com.cool.request.springmvc.HttpRequestInfo;
import com.intellij.psi.PsiMethod;

public interface RequestParamSpeculate {
     void set(PsiMethod method, HttpRequestInfo httpRequestInfo);
}
