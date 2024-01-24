package com.cool.request.springmvc.param;

import com.cool.request.net.FormDataInfo;
import com.cool.request.net.MediaTypes;
import com.cool.request.springmvc.HttpRequestInfo;
import com.cool.request.springmvc.utils.ParamUtils;
import com.cool.request.utils.StringUtils;
import com.hxl.utils.openapi.Type;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormDataSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        if (!ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;
        List<FormDataInfo> param = new ArrayList<>();
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            if (ParamUtils.isHttpServlet(parameter)) continue;
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            //将所有参数都放在multipart/form-data里面

            //获取将参数放在url里面，二选一
            Map<String, String> psiAnnotationValues = ParamUtils.getPsiAnnotationValues(requestParam);
            String value = psiAnnotationValues.get("value");
            if (StringUtils.isEmpty(value)) value = parameter.getName();
            param.add(new FormDataInfo(value, "", ParamUtils.isMultipartFile(parameter) ? Type.file.getTargetValue() : "text"));
        }
        if (!param.isEmpty()) {
            httpRequestInfo.setContentType(MediaTypes.MULTIPART_FORM_DATA);
            httpRequestInfo.setFormDataInfos(param);
        }
    }
}
