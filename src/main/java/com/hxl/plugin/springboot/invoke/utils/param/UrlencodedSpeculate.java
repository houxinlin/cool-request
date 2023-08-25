package com.hxl.plugin.springboot.invoke.utils.param;

import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class UrlencodedSpeculate   extends BasicUrlParamSpeculate  implements RequestParamSpeculate{
    @Override
    public void set(PsiMethod method, RequestCache.RequestCacheBuilder requestCacheBuilder) {
        if (!ParamUtils.isGetRequest(method) && !ParamUtils.hasMultipartFile(method.getParameterList().getParameters())){
            List<KeyValue> param = new ArrayList<>(super.get(method, requestCacheBuilder));
            requestCacheBuilder.withUrlencodedBody(param);
        }
    }
}
