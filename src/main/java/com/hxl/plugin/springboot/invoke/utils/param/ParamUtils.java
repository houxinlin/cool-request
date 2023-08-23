package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamUtils {
    private static final List<String> BASE_TYPE = List.of("java.lang.String", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Boolean","java.lang.Character",
            "int", "long","float", "double", "boolean","char");
    public  static boolean isBaseType(String name){
        if (name==null) return false;
        return BASE_TYPE.contains(name);
    }
    public static boolean isBoolean(String name) {
        return "java.lang.Double".equals(name) || "boolean".equals(name);
    }
    public  static boolean isNumber(String name){
        if (name==null) return false;
        return "java.lang.Integer".equals(name) || "java.lang.Long".equals(name) || "int".equals(name) || "long".equals(name);
    }
    public  static boolean isFloat(String name){
        if (name==null) return false;
        return "java.lang.Float".equals(name) || "java.lang.Double".equals(name)|| "float".equals(name) || "double".equals(name);
    }
    public  static boolean isString(String name){
        if (name==null) return false;
        return "java.lang.String".equals(name);
    }
    public static boolean isJdkClass(String name){
        if (name==null) return false;
        return name.startsWith("java");
    }
    public static boolean isArray(String name){
        if (name==null) return false;
        return name.endsWith("[]");
    }
    public static boolean isMultipartFile(PsiParameter parameter){
        return parameter.getType().getCanonicalText().equals("org.springframework.web.multipart.MultipartFile");
    }

    public static boolean isNotGetRequest(PsiMethod psiMethod){
        PsiAnnotation getAnnotation = psiMethod.getAnnotation("org.springframework.web.bind.annotation.GetMapping");
        PsiAnnotation requestMappingAnnotation = psiMethod.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        if (getAnnotation==null && requestMappingAnnotation ==null) return true;
        if (getAnnotation ==null){
            System.out.println(getPsiAnnotationValues(requestMappingAnnotation).get("method"));
            return true;
        }
        return true;
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
