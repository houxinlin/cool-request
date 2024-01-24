package com.cool.request.script;

import com.cool.request.script.HTTPRequest;
import com.cool.request.script.ILog;

class CoolRequestScript {
    private HTTPRequest request;
    private ILog log;

    /**
     * do not delete
     *
     * @param log     log print
     * @param request request context
     */
    public CoolRequestScript(ILog log, HTTPRequest request) {
        this.request = request;
        this.log = log;
    }

    /**
     * Write your code here
     */
    public void handlerRequest() {
    }
}