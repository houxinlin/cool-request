package com.cool.request.lib.springmvc.param;

import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.intellij.psi.PsiMethod;

public interface RequestParamSpeculate {
     void set(PsiMethod method, HttpRequestInfo httpRequestInfo);
}
