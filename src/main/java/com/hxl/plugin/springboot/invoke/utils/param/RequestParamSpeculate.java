package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.intellij.psi.PsiMethod;

public interface RequestParamSpeculate {
    public void set(PsiMethod method,RequestCache.RequestCacheBuilder requestCacheBuilder);
}
