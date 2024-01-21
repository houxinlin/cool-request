package com.hxl.plugin.springboot.invoke.script;

public class RequestApi {
    private Request request;
    private ILog log;

    /**
     * do not delete
     *
     * @param log     log print
     * @param request request context
     */
    public RequestApi(ILog log, Request request) {
        this.request = request;
        this.log = log;
    }

    /**
     * Write your code here
     */
    public void handlerRequest() {
    }
}