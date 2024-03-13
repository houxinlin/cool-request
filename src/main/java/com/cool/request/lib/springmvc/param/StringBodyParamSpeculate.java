package com.cool.request.lib.springmvc.param;

import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.StringGuessBody;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.List;

public class StringBodyParamSpeculate extends BasicBodySpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        List<PsiParameter> parameters = listCanSpeculateParam(method);
        //非GET和具有表单的请求不设置此选项

        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(parameters)) {
            if (parameters.size() == 1 && ParamUtils.isString(parameters.get(0).getType().getCanonicalText())) {
                httpRequestInfo.setRequestBody(new StringGuessBody(""));
                httpRequestInfo.setContentType(MediaTypes.TEXT);
                return;
            }

            //没有RequestBody注解，不要设置任何参数
            PsiParameter requestBodyPsiParameter = ParamUtils.getParametersWithAnnotation(method, "org.springframework.web.bind.annotation.RequestBody");
            if (requestBodyPsiParameter != null) {
                if (ParamUtils.isString(requestBodyPsiParameter.getType().getCanonicalText())) {
                    httpRequestInfo.setRequestBody(new StringGuessBody(""));
                    httpRequestInfo.setContentType(MediaTypes.TEXT);
                }
            }
        }
    }
}
