package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public class UrlParamSpeculate  implements RequestParamSpeculate{
    private static final List<String> BASE_TYPE=List.of("java.lang.String","java.lang.Integer","java.lang.Long","java.lang.Float","java.lang.Double","java.lang.Boolean",
            "int","float","double","boolean");
    @Override
    public void set(PsiMethod method, RequestCache.RequestCacheBuilder requestCacheBuilder) {
        List<KeyValue> param =new ArrayList<>();

        for (PsiAnnotation annotation : method.getAnnotations()) {
            //??get??????
            if ("org.springframework.web.bind.annotation.GetMapping".equalsIgnoreCase(annotation.getQualifiedName())){
                for (PsiParameter parameter : method.getParameterList().getParameters()) {
                    PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
                    if (requestParam!=null){
                        for (JvmAnnotationAttribute attribute : requestParam.getAttributes()) {
                            String literalValue = ((PsiNameValuePair) attribute).getLiteralValue(); //@RequestParam??
                            param.add(new KeyValue(literalValue,""));
                        }
                        continue;
                    }
                    //???
                    if (BASE_TYPE.contains(parameter.getType().getCanonicalText())) {
                        param.add(new KeyValue(parameter.getName(),""));
                    }
                }
            }
        }
        requestCacheBuilder.withUrlParams(param);
    }
}
