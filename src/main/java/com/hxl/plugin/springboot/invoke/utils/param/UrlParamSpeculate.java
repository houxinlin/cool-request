package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlParamSpeculate implements RequestParamSpeculate {
    private static final List<String> BASE_TYPE = List.of("java.lang.String", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Boolean",
            "int", "float", "double", "boolean");
    private static final List<String> SPRING_MVC_PARAM = List.of("RequestParam", "RequestParam", "RequestHeader", "RequestAttribute", "CookieValue", "MatrixVariable", "ModelAttribute", "PathVariable");
    private static final String ANNOTATION_PREFIX = "org.springframework.web.bind.annotation.";

    private boolean hasParamAnnotation(PsiParameter parameter) {
        for (String annotationParam : SPRING_MVC_PARAM) {
            if (parameter.getAnnotation(ANNOTATION_PREFIX.concat(annotationParam)) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void set(PsiMethod method, RequestCache.RequestCacheBuilder requestCacheBuilder) {
        List<KeyValue> param = new ArrayList<>();
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            //??@RequestParam && ??MultipartFile
            if (requestParam != null && !ParamUtils.isMultipartFile(parameter)) {
                Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
                String value = psiAnnotationValues.get("value");
                if (StringUtils.isEmpty(value)) value = parameter.getName();
                param.add(new KeyValue(value, ""));
                continue;
            }
            //??????
            if (!hasParamAnnotation(parameter)) {
                //??????????????
                if (BASE_TYPE.contains(parameter.getType().getCanonicalText())) {
                    param.add(new KeyValue(parameter.getName(), ""));
                }
            }

        }
        requestCacheBuilder.withUrlParams(param);
    }
}
