package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.net.request.StandardHttpRequestParam;

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
