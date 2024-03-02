package com.cool.request.lib.springmvc.param;

import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.RequestParameterDescription;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class UrlParamSpeculate extends BasicUrlParameterSpeculate implements RequestParamSpeculate {
    public UrlParamSpeculate() {

    }

    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        //如果不是Get请求则退出
        if (ParamUtils.hasMultipartFile(method.getParameterList().getParameters())) return;
        if (ParamUtils.isNotGetRequest(method)) {
            //如果不是GET请求，并且请求体不是APPLICATION_WWW_FORM
            if (!MediaTypes.APPLICATION_WWW_FORM.equalsIgnoreCase(httpRequestInfo.getContentType())) {
                httpRequestInfo.setUrlParams(super.get(method, true));
            }
        } else {
            List<RequestParameterDescription> requestParameterDescriptions = super.get(method, false);
            httpRequestInfo.setUrlParams(requestParameterDescriptions);
        }
    }
}
