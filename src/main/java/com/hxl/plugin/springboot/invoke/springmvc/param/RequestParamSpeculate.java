package com.hxl.plugin.springboot.invoke.springmvc.param;

import com.hxl.plugin.springboot.invoke.springmvc.HttpRequestInfo;
import com.intellij.psi.PsiMethod;

public interface RequestParamSpeculate {
     void set(PsiMethod method, HttpRequestInfo httpRequestInfo);
}
