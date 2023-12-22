package com.hxl.plugin.springboot.invoke.springmvc.utils;

import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamUtils {
    private static final List<String> SPRING_MVC_PARAM = List.of("RequestBody", "RequestParam", "RequestHeader", "RequestAttribute", "CookieValue", "MatrixVariable", "ModelAttribute", "PathVariable");
    private static final String ANNOTATION_PREFIX = "org.springframework.web.bind.annotation.";
    private static final List<String> BASE_TYPE = List.of("java.lang.String", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Boolean", "java.lang.Character",
            "int", "long", "float", "double", "boolean", "char");

    public static boolean isBaseType(String name) {
        if (name == null) return false;
        return BASE_TYPE.contains(name);
    }

    public static boolean isBoolean(String name) {
        return "java.lang.Double".equals(name) || "boolean".equals(name);
    }

    public static boolean isNumber(String name) {
        if (name == null) return false;
        return "java.lang.Integer".equals(name) || "java.lang.Long".equals(name) || "int".equals(name) || "long".equals(name);
    }

    public static boolean isFloat(String name) {
        if (name == null) return false;
        return "java.lang.Float".equals(name) || "java.lang.Double".equals(name) || "float".equals(name) || "double".equals(name);
    }

    public static boolean isString(String name) {
        if (name == null) return false;
        return "java.lang.String".equals(name);
    }

    public static boolean isJdkClass(String name) {
        if (name == null) return false;
        return name.startsWith("java") || name.startsWith("jdk") || name.startsWith("javax");
    }

    public static boolean isUserObject(String name) {
        return !ParamUtils.isArray(name) &&
                !ParamUtils.isBaseType(name) &&
                !ParamUtils.isJdkClass(name);
    }

    public static boolean isArray(String name) {
        if (name == null) return false;
        return name.endsWith("[]");
    }

    public static boolean isChar(String name) {
        if (name == null) return false;
        return "java.lang.Character".equals(name) || "char".equals(name);
    }

    public static boolean isMap(PsiField psiField) {
        if (psiField == null) return false;
        if (psiField.getType().getCanonicalText().startsWith("java.util.Map")) return true;
        PsiType[] superTypes = psiField.getType().getSuperTypes();
        for (PsiType superType : superTypes) {
            String canonicalText = superType.getCanonicalText();
            if (canonicalText.startsWith("java.util.Map")) return true;
        }
        return false;
    }

    public static boolean isList(PsiField psiField) {
        if (psiField == null) return false;
        if (psiField.getType().getCanonicalText().startsWith("java.util.List")) return true;

        for (PsiType superType : psiField.getType().getSuperTypes()) {
            if (superType.getCanonicalText().startsWith("java.util.List")) return true;
        }
        return false;
    }

    public static String getListGenerics(PsiField psiField) {
        if (!isList(psiField)) return null;
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(psiField.getType().getCanonicalText());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static boolean isMultipartFile(PsiParameter parameter) {
        return parameter.getType().getCanonicalText().equals("org.springframework.web.multipart.MultipartFile");
    }

    public static boolean hasMultipartFile(PsiParameter[] parameters) {
        for (PsiParameter parameter : parameters) {
            if (isMultipartFile(parameter)) return true;
        }
        return false;
    }

    public static boolean hasSpringParamAnnotation(PsiParameter parameter) {
        return hasSpringParamAnnotation(parameter, null);
    }

    public static boolean hasRequestBody(PsiParameter parameter) {
        if (parameter.getAnnotation(ANNOTATION_PREFIX.concat("RequestBody")) != null) {
            return true;
        }
        return false;
    }

    public static boolean hasRequestBody(PsiMethod method) {
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            if (hasRequestBody(parameter)) return true;
        }
        return false;
    }

    public static boolean hasSpringParamAnnotation(PsiParameter parameter, String ignore) {
        for (String annotationParam : SPRING_MVC_PARAM) {
            if (annotationParam.equals(ignore)) continue;
            if (parameter.getAnnotation(ANNOTATION_PREFIX.concat(annotationParam)) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotGetRequest(PsiMethod psiMethod) {
        return !isGetRequest(psiMethod);
    }

    public static boolean isGetRequest(PsiMethod psiMethod) {
        PsiAnnotation getAnnotation = psiMethod.getAnnotation("org.springframework.web.bind.annotation.GetMapping");
        if (getAnnotation != null) return true;

        PsiAnnotation requestMappingAnnotation = psiMethod.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        if (requestMappingAnnotation != null) {
            String method = getPsiAnnotationValues(requestMappingAnnotation).get("method");
            if (method == null) return true;
            return "RequestMethod.GET".equalsIgnoreCase(method);
        }
        return false;
    }

    public static Map<String, String> getPsiAnnotationValues(PsiAnnotation psiAnnotation) {
        Map<String, String> result = new HashMap<>();
        if (psiAnnotation == null) return result;
        for (JvmAnnotationAttribute attribute : psiAnnotation.getAttributes()) {
            PsiNameValuePair psiNameValuePair = (PsiNameValuePair) attribute;
            String value = psiNameValuePair.getValue().getText();
            String literalValue = psiNameValuePair.getLiteralValue();
            String finalValue = null;
            if (!StringUtils.isEmpty(literalValue)) finalValue = literalValue;
            if (StringUtils.isEmpty(finalValue)) finalValue = value;
            result.put(psiNameValuePair.getAttributeName(), finalValue);
        }
        return result;
    }


    public static String getParameterType(PsiParameter parameter) {
        String canonicalText = parameter.getType().getCanonicalText();
        if (isBaseType(canonicalText)) {
            if (!canonicalText.startsWith("java")) return canonicalText.toLowerCase();
        }
        String[] split = canonicalText.split("\\.");
        if (split.length > 0) return split[split.length - 1].toLowerCase();
        return "";
    }

    public static boolean hasSpringMvcRequestParamAnnotation(PsiMethod method) {
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            if (parameter.getAnnotation(ANNOTATION_PREFIX.concat("RequestParam")) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasUserObject(PsiMethod method) {
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            if (isUserObject(parameter.getType().getCanonicalText())) return true;
        }
        return false;
    }

    public static boolean hasBaseType(PsiMethod method) {
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            if (!isBaseType(parameter.getType().getCanonicalText())) return true;
        }
        return false;
    }

    public static boolean isHttpServlet(PsiParameter parameter) {
        return parameter.getType().getCanonicalText().startsWith("javax.servlet.http")
                || parameter.getType().getCanonicalText().equals("jakarta.servlet.http");
    }
}
