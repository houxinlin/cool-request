package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;

import java.util.HashMap;
import java.util.Map;

public class ParamUtils {
    public static boolean isMultipartFile(PsiParameter parameter){
        return parameter.getType().getCanonicalText().equals("org.springframework.web.multipart.MultipartFile");
    }

    public static Map<String,String> getPsiAnnotationValues(PsiAnnotation psiAnnotation){
        Map<String,String> result =new HashMap<>();
        if (psiAnnotation==null) return result;
        for (JvmAnnotationAttribute attribute : psiAnnotation.getAttributes()) {
            PsiNameValuePair psiNameValuePair = (PsiNameValuePair) attribute;
            result.put(psiNameValuePair.getAttributeName(),psiNameValuePair.getLiteralValue());
        }
        return result;
    }
}
