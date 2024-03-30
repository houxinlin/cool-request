package com.cool.request.scan;

import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.utils.PsiUtils;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class SpringMvcHttpMethodParser implements HttpMethodParser {
    @Override
    public List<HttpMethod> parserHttpMethod(PsiMethod method) {
        return PsiUtils.getHttpMethod(method);
    }
}
