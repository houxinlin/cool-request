package com.cool.request.script;

import com.cool.request.script.HTTPResponse;
import com.cool.request.script.ILog;

class CoolResponseScript {

    /**
     * 1.Only classes under Java packages can be used here,
     * 2.modifying class and method names or parameters is not allowed
     * 3.If false is returned, the HTTP request will be terminated
     *
     * @param log      Log output
     * @param response The response result of HTTP
     */
    public void handlerResponse(ILog log, HTTPResponse response) {
        /**
         * Write your code here
         */
        //log.println(response.getCode());

    }
}