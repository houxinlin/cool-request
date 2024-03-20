package com.cool.request.view.main;

import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.RequestContext;

/**
 * 请求发起和结束事件监听器
 */
public interface HTTPEventListener {
    /**
     * 开始发送
     */
    public default void beginSend(RequestContext requestContext){}

    /**
     * 结束发送
     */
    public default void endSend(RequestContext requestContext, HTTPResponseBody httpResponseBody){}

}
