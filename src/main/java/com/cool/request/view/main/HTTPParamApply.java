package com.cool.request.view.main;

import com.cool.request.components.http.net.request.StandardHttpRequestParam;

public interface HTTPParamApply {

    /**
     * 后置应用
     *
     */
    public void postApplyParam(StandardHttpRequestParam standardHttpRequestParam);
}
