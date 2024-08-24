/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ParamUtils.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.lib.springmvc.utils;

import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.utils.StringUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.*;

import java.util.*;
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
        return "java.lang.Boolean".equalsIgnoreCase(name) || "boolean".equalsIgnoreCase(name);
    }

    public static boolean isNumber(String name) {
        if (name == null) return false;
        return "java.lang.Integer".equalsIgnoreCase(name) || "java.lang.Long".equalsIgnoreCase(name) ||
                "int".equalsIgnoreCase(name) || "long".equalsIgnoreCase(name) || "integer".equalsIgnoreCase(name)
                || "short".equalsIgnoreCase(name) || "java.lang.Short".equalsIgnoreCase(name);
    }

    public static boolean isFloat(String name) {
        if (name == null) return false;
        return "java.lang.Float".equalsIgnoreCase(name) ||
                "java.lang.Double".equalsIgnoreCase(name) ||
                "float".equalsIgnoreCase(name) ||
                "double".equalsIgnoreCase(name);
    }

    public static boolean isString(String name) {
        if (name == null) return false;
        return "java.lang.String".equalsIgnoreCase(name) || "String".equalsIgnoreCase(name);
    }

    public static boolean isJdkClass(String name) {
        if (name == null) return false;
        return name.startsWith("java") || name.startsWith("jdk") || name.startsWith("javax");
    }

    public static boolean isUserObject(String name) {
        return !ParamUtils.isBaseType(name) &&
                !ParamUtils.isJdkClass(name);
    }

    public static boolean isSpringBoot(String name) {
        if (StringUtils.isEmpty(name)) return false;
        return name.startsWith("org.springframework");
    }

    public static boolean isHttpServlet(String name) {
        if (StringUtils.isEmpty(name)) return false;
        if (name.startsWith("jakarta.servlet.http")) return true;
        return name.startsWith("javax.servlet.http");
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
        return "java.lang.Byte".equalsIgnoreCase(name) || "byte".equalsIgnoreCase(name);
    }

    public static boolean isChar(String name) {
        if (name == null) return false;
        return "java.lang.Character".equalsIgnoreCase(name) || "char".equalsIgnoreCase(name);
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

    public static boolean isList(PsiType psiType) {
        if (psiType == null) return false;
        if (psiType.getCanonicalText().startsWith("java.util.List")) return true;
        for (PsiType superType : psiType.getSuperTypes()) {
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

    public static String getListGenerics(PsiType psiType) {
        if (!isList(psiType)) return null;
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(psiType.getCanonicalText());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static boolean isMultipartFile(PsiParameter parameter) {
        return parameter.getType().getCanonicalText().equalsIgnoreCase("org.springframework.web.multipart.MultipartFile");
    }

    public static boolean hasMultipartFile(PsiParameter[] parameters) {
        for (PsiParameter parameter : parameters) {
            if (isMultipartFile(parameter)) return true;
        }
        return false;
    }

    public static boolean hasMultipartFile(List<PsiParameter> parameters) {
        for (PsiParameter parameter : parameters) {
            if (isMultipartFile(parameter)) return true;
        }
        return false;
    }

    public static boolean hasRequestPart(PsiParameter[] parameters) {
        for (PsiParameter parameter : parameters) {
            if (parameter.getAnnotation("org.springframework.web.bind.annotation.RequestPart") != null) {
                return true;
            }
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

    public static List<String> gePsiAnnotationValuesAsString(PsiAnnotation psiAnnotation, String key) {
        PsiAnnotationMemberValue value = psiAnnotation.findAttributeValue(key);
        if (value == null) return new ArrayList<>();
        return gePsiAnnotationValuesAsString(value);
    }

    private static List<String> gePsiAnnotationValuesAsString(PsiAnnotationMemberValue psiAnnotationMemberValue) {
        if (psiAnnotationMemberValue instanceof PsiReferenceExpression) {
            return Collections.singletonList(psiAnnotationMemberValue.getText());
        }
        if (psiAnnotationMemberValue instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) psiAnnotationMemberValue).getInitializers();
            List<String> result = new ArrayList<>();
            for (PsiAnnotationMemberValue initializer : initializers) {
                result.addAll(gePsiAnnotationValuesAsString(initializer));
            }
            return result;
        }
        return new ArrayList<>();
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

    public static boolean isSpringBoot(PsiParameter parameter) {
        return parameter.getType().getCanonicalText().startsWith("org.springframework");
    }

    public static PsiParameter getParametersWithAnnotation(PsiMethod psiMethod, String annotation) {
        for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
            if (parameter.getAnnotation(annotation) != null) return parameter;
        }
        return null;
    }

    public static List<String> getHttpUrlFromPsiAnnotationWithSpringMvc(PsiAnnotation psiAnnotation) {
        return getHttpUrlFromPsiAnnotation(psiAnnotation, "value", "path");
    }

    public static List<String> getHttpUrlFromPsiAnnotationWithJaxRs(PsiAnnotation psiAnnotation) {
        return getHttpUrlFromPsiAnnotation(psiAnnotation, "value");
    }

    public static List<String> getHttpUrlFromPsiAnnotationWithRose(PsiAnnotation psiAnnotation) {
        return getHttpUrlFromPsiAnnotation(psiAnnotation, "value");
    }

    private static List<String> getHttpUrlFromPsiAnnotation(PsiAnnotation psiAnnotation, String... attributeName) {
        if (psiAnnotation == null) return Collections.EMPTY_LIST;
        List<PsiAnnotationMemberValue> psiAnnotationMemberValueList = new ArrayList<>();
        for (String name : attributeName) {
            psiAnnotationMemberValueList.add(psiAnnotation.findAttributeValue(name));
        }
        List<String> result = new ArrayList<>();
        for (PsiAnnotationMemberValue psiAnnotationMemberValue : psiAnnotationMemberValueList) {
            if (psiAnnotationMemberValue == null) continue;
            result.addAll(getPsiAnnotationMemberValueIfString(psiAnnotationMemberValue));
        }
        return result;
    }

    public static boolean isEquals(List<String> source, List<String> target) {
        if (source == null || target == null) return false;
        if (source.size() != target.size()) return false;
        return source.equals(target);

    }

    private static List<String> getPsiAnnotationMemberValueIfString(PsiAnnotationMemberValue psiAnnotationMemberValue) {
        if (psiAnnotationMemberValue instanceof PsiLiteral) {
            Object propertyValue = ((PsiLiteral) psiAnnotationMemberValue).getValue();
            if (propertyValue instanceof String) return List.of(propertyValue.toString());

        }
        List<String> result = new ArrayList<>();

        if (psiAnnotationMemberValue instanceof PsiArrayInitializerMemberValue) {
            for (PsiAnnotationMemberValue value : ((PsiArrayInitializerMemberValue) psiAnnotationMemberValue).getInitializers()) {
                List<String> temp = getPsiAnnotationMemberValueIfString(value);
                if (temp != null && !temp.isEmpty()) {
                    for (String item : temp) {
                        if (item != null) {
                            result.add(item);
                        }
                    }
                }
            }
        }
        if (psiAnnotationMemberValue instanceof PsiReferenceExpression) {
            PsiElement resolve = ((PsiReferenceExpression) psiAnnotationMemberValue).resolve();
            if (resolve instanceof PsiField) {
                PsiField psiField = (PsiField) resolve;
                PsiExpression initializer = psiField.getInitializer();
                List<String> temp = getPsiAnnotationMemberValueIfString(initializer);
                if (temp != null && !temp.isEmpty()) {
                    for (String item : temp) {
                        if (item != null) {
                            result.add(item);
                        }
                    }
                }
            }
        }
        if (psiAnnotationMemberValue instanceof PsiPolyadicExpression) {
            String urlValue = "";
            PsiExpression[] operands = ((PsiPolyadicExpression) psiAnnotationMemberValue).getOperands();
            for (PsiExpression operand : operands) {
                List<String> value = getPsiAnnotationMemberValueIfString(operand);
                if (value.size() == 1) {
                    urlValue += value.get(0);
                }
            }
            result.add(urlValue);
        }
        return result;

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

    public static String getAnnotationStringValue(PsiAnnotation psiAnnotation, String value) {
        if (psiAnnotation == null) return null;
        PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.findAttributeValue(value);
        if (psiAnnotationMemberValue == null) return null;

        List<String> psiAnnotationMemberValueIfString = getPsiAnnotationMemberValueIfString(psiAnnotationMemberValue);
        if (!psiAnnotationMemberValueIfString.isEmpty()) {
            return psiAnnotationMemberValueIfString.get(0);
        }
        return null;
    }

    public static Iterable<? extends PsiField> listCanApplyJsonField(PsiClass psiClass) {
        List<PsiField> result = new ArrayList<>();
        for (PsiField psiField : psiClass.getAllFields()) {
            if (isInstance(psiField)) result.add(psiField);
        }
        return result;
    }

    public static boolean isInstance(PsiField field) {
        return !field.hasModifierProperty(PsiModifier.STATIC);
    }

    public static boolean isArrayOrList(PsiParameter parameter) {
        if (ParamUtils.isArray(parameter.getType().getCanonicalText())) {
            return true;
        }
        if (ParamUtils.isList(parameter.getType())) {
            return true;
        }
        return false;
    }

    public static String getRealClassName(PsiParameter parameter) {
        if (ParamUtils.isArray(parameter.getType().getCanonicalText())) {
            return parameter.getType().getCanonicalText().replace("[]", "");
        }
        if (ParamUtils.isList(parameter.getType())) {
            return ParamUtils.getListGenerics(parameter.getType());
        }
        return parameter.getType().getCanonicalText();
    }
}
