package com.cool.request.lib.springmvc.param;

import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.utils.ParamUtils;
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
        //无法推测其余参数是不是form-data，只有存在file的时候可以断定是form-data请求
        if (!ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;
        List<FormDataInfo> param = new ArrayList<>();
        for (PsiParameter parameter : listCanSpeculateParam(method)) {
            PsiAnnotation requestParam = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            //将所有参数都放在multipart/form-data里面

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
