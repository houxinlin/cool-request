package com.cool.request.script;

public class CoolResponseScript {
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

    public void handlerResponse() {

    }
}