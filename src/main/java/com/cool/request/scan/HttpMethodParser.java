package com.cool.request.scan;

import com.cool.request.components.http.net.HttpMethod;
import com.intellij.psi.PsiMethod;

import java.util.List;

public interface HttpMethodParser {
    public List<HttpMethod> parserHttpMethod(PsiMethod method);
}
