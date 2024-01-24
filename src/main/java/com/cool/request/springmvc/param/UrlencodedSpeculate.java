package com.cool.request.springmvc.param;

import com.cool.request.net.MediaTypes;
import com.cool.request.springmvc.HttpRequestInfo;
import com.cool.request.springmvc.RequestParameterDescription;
import com.cool.request.springmvc.utils.ParamUtils;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class UrlencodedSpeculate extends BasicUrlParameterSpeculate implements RequestParamSpeculate {
    @Override
    public void set(PsiMethod method, HttpRequestInfo httpRequestInfo) {
        //比如是非GET情况，没有MultipartFile文件
        if (!ParamUtils.isGetRequest(method) &&
                !ParamUtils.hasMultipartFile(method.getParameterList().getParameters())){
            if (ParamUtils.hasRequestBody(method)) return;

            List<RequestParameterDescription> param = new ArrayList<>(super.get(method));
            if (!param.isEmpty()){
                httpRequestInfo.setUrlencodedBody(param);
                httpRequestInfo.setContentType(MediaTypes.APPLICATION_WWW_FORM);
            }
        }
    }
}
