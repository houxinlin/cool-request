package com.cool.request.component.http.invoke;

import com.cool.request.component.http.net.request.ReflexHttpRequestParamAdapter;
import com.cool.request.utils.GsonUtils;

public class ReflexControllerRequest extends BasicRemoteComponentRequest<ReflexHttpRequestParamAdapter> {
    public ReflexControllerRequest(int port) {
        super(port);
    }

    @Override
    public String createMessage(ReflexHttpRequestParamAdapter reflexHttpRequestParam) {
        return GsonUtils.toJsonString(reflexHttpRequestParam);
    }

}
