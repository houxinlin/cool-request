package com.hxl.plugin.springboot.invoke.springmvc.utils;

import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamUtils {
    private static final List<String> SPRING_MVC_PARAM = List.of("RequestBody", "RequestParam", "RequestHeader",
            "RequestAttribute", "CookieValue", "MatrixVariable", "ModelAttribute", "PathVariable");
    private static final String ANNOTATION_PREFIX = "org.springframework.web.bind.annotation.";
    private static final List<String> BASE_TYPE = List.of("java.lang.String", "java.lang.Integer", "java.lang.Short", "java.lang.Byte",
            "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Boolean", "java.lang.Character",
            "int", "long", "float", "double", "boolean", "char", "short", "byte");

    public static boolean isBaseType(String name) {
        if (name == null) return false;
        return BASE_TYPE.contains(name);
    }

    public static boolean isBoolean(String name) {
        return "java.lang.Boolean".equals(name) || "boolean".equals(name);
    }

    public static boolean isNumber(String name) {
        if (name == null) return false;
        return "java.lang.Integer".equals(name) || "java.lang.Long".equals(name) ||
                "int".equals(name) || "long".equals(name)
                || "short".equals(name) || "java.lang.Short".equals(name);
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
        return !ParamUtils.isBaseType(name) &&
                !ParamUtils.isJdkClass(name);
    }

    public static String getStandardClassName(String name) {
        if (StringUtils.isEmpty(name)) return null;
        if (name.endsWith("[]")) return name.replace("[]", "");
        return name;
    }

    public static boolean isArray(String name) {
        if (name == null) return false;
        return name.endsWith("[]");
    }

    private static boolean isByte(String name) {
        if (name == null) return false;
        return "java.lang.Byte".equals(name) || "byte".equals(name);
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
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.GetMapping", "RequestMethod.GET");
    }

    public static boolean isPostRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.PostMapping", "RequestMethod.POST");
    }

    public static boolean isHeadRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, null, "RequestMethod.HEAD");
    }

    public static boolean isPutRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.PutMapping", "RequestMethod.PUT");
    }

    public static boolean isPatchRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.PatchMapping", "RequestMethod.PATCH");
    }

    public static boolean isDeleteRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, "org.springframework.web.bind.annotation.DeleteMapping", "RequestMethod.DELETE");
    }

    public static boolean isOptionRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, null, "RequestMethod.OPTIONS");
    }

    public static boolean isTraceRequest(PsiMethod psiMethod) {
        return isHttpRequestMethod(psiMethod, null, "RequestMethod.TRACE");
    }

    public static String[] getHttpUrl(PsiMethod psiMethod) {
        if (isGetRequest(psiMethod)) return getHttpUrl("org.springframework.web.bind.annotation.GetMapping", psiMethod);
        if (isPutRequest(psiMethod)) return getHttpUrl("org.springframework.web.bind.annotation.PutMapping", psiMethod);
        if (isPostRequest(psiMethod))
            return getHttpUrl("org.springframework.web.bind.annotation.PostMapping", psiMethod);
        if (isDeleteRequest(psiMethod))
            return getHttpUrl("org.springframework.web.bind.annotation.DeleteMapping", psiMethod);
        if (isPatchRequest(psiMethod))
            return getHttpUrl("org.springframework.web.bind.annotation.PatchMapping", psiMethod);

        if (isOptionRequest(psiMethod) || isHeadRequest(psiMethod) || isTraceRequest(psiMethod))
            return getHttpUrl(null, psiMethod);
        return null;
    }

    public static boolean isHttpRequestMethod(PsiMethod psiMethod, String mappingName, String httpMethod) {
        if (mappingName != null) {
            PsiAnnotation getAnnotation = psiMethod.getAnnotation(mappingName);
            if (getAnnotation != null) return true;
        }
        PsiAnnotation requestMappingAnnotation = psiMethod.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        if (requestMappingAnnotation != null) {
            String method = getPsiAnnotationValues(requestMappingAnnotation).get("method");
            if (method == null) return true;
            return httpMethod.equalsIgnoreCase(method);
        }
        return false;
    }

    public static boolean hasHttpMethod(PsiMethod psiMethod) {
        if (ParamUtils.isGetRequest(psiMethod)) return true;
        if (ParamUtils.isPutRequest(psiMethod)) return true;
        if (ParamUtils.isPostRequest(psiMethod)) return true;
        if (ParamUtils.isDeleteRequest(psiMethod)) return true;
        if (ParamUtils.isTraceRequest(psiMethod)) return true;
        if (ParamUtils.isOptionRequest(psiMethod)) return true;
        if (ParamUtils.isHeadRequest(psiMethod)) return true;
        if (ParamUtils.isPatchRequest(psiMethod)) return true;
        return false;
    }

    public static Map<String, String> getPsiAnnotationValues(PsiAnnotation psiAnnotation) {
        Map<String, String> result = new HashMap<>();
        if (psiAnnotation == null) return result;
        for (JvmAnnotationAttribute attribute : psiAnnotation.getAttributes()) {
            if (!(attribute instanceof PsiNameValuePair)) continue;
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

    private static String[] getHttpUrl(String mappingName, PsiMethod psiMethod) {
        Map<String, String> psiAnnotationValues = null;

        if (mappingName != null) {
            PsiAnnotation getAnnotation = psiMethod.getAnnotation(mappingName);
            if (getAnnotation != null) {
                psiAnnotationValues = getPsiAnnotationValues(getAnnotation);
            }
        }
        PsiAnnotation requestMappingAnnotation = psiMethod.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        if (requestMappingAnnotation != null) {
            psiAnnotationValues = getPsiAnnotationValues(requestMappingAnnotation);
        }

        if (psiAnnotationValues != null) {
            String value = psiAnnotationValues.get("value");
            if (value == null) return new String[]{""};

            if (!value.startsWith("{"))
                return new String[]{value.replaceAll("^\"|\"$", "")};

            StringBuilder values = new StringBuilder(value).deleteCharAt(0);
            values.deleteCharAt(values.length() - 1);
            String[] split = values.toString().split(",");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].replaceAll("^\"|\"$", "");
            }
            return split;
        }
        return null;
    }


    public static boolean isEquals(List<String> source, List<String> target) {
        if (source == null || target == null) return false;
        if (source.size() != target.size()) return false;
        return source.equals(target);

    }


    public static boolean httpMethodIn(List<HttpMethod> list, HttpMethod target) {
        for (HttpMethod httpMethod : list) {
            if (httpMethod.equals(target)) return true;
        }
        return false;
    }

    public static Object getDefaultValueByClassName(String className, Object defaultValue) {
        if (ParamUtils.isChar(className)) return '\0';
        if (ParamUtils.isString(className)) return "";
        if (ParamUtils.isNumber(className)) return 0;
        if (ParamUtils.isFloat(className)) return 0.0f;
        if (ParamUtils.isBoolean(className)) return false;
        if (ParamUtils.isByte(className)) return 0;
        return defaultValue;
    }


}
