package com.cool.request.utils;

import com.cool.request.net.request.StandardHttpRequestParam;

public interface HTTPParamApply {
    /**
     * 前置应用
     */
    public void preApplyParam(StandardHttpRequestParam standardHttpRequestParam);

    /**
     * 后置应用
     *
     */
    public void postApplyParam(StandardHttpRequestParam standardHttpRequestParam);
}
