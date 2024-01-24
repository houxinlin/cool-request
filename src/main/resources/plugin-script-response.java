package com.cool.request.script;

import com.cool.request.script.HTTPResponse;
import com.cool.request.script.ILog;

class CoolResponseScript {
    private HTTPResponse response;
    private ILog log;

    /**
     * do not delete
     *
     * @param log      log print
     * @param response response context
     */
    public CoolResponseScript(ILog log, HTTPResponse response) {
        this.response = response;
        this.log = log;
    }
    /**
     * Write your code here
     */
    public void handlerResponse() {

    }
}