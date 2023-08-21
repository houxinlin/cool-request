package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;

public class HeaderParamSpeculate  implements RequestParamSpeculate{
    @Override
    public void set(PsiMethod method, RequestCache.RequestCacheBuilder requestCacheBuilder) {
        List<KeyValue> param =new ArrayList<>();
//
//        for (PsiAnnotation annotation : method.getAnnotations()) {
//            if ("org.springframework.web.bind.annotation.GetMapping".equalsIgnoreCase(annotation.getQualifiedName())){
//                for (PsiParameter parameter : method.getParameterList().getParameters()) {
//                    PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
//                    if (requestParam!=null){
//                        for (JvmAnnotationAttribute attribute : requestParam.getAttributes()) {
//                            String literalValue = ((PsiNameValuePair) attribute).getLiteralValue(); //@RequestParam??
//                            param.add(new KeyValue(literalValue,""));
//                        }
//                        continue;
//                    }
//                    String canonicalText = parameter.getType().getCanonicalText();
////                    if (BASE_TYPE.contains(parameter.getType().getCanonicalText())) {
////                        param.add(new KeyValue(parameter.getName(),""));
////                    }
//
//                }
//            }
//        }
//        requestCacheBuilder.withUrlParams(param);
    }
}
