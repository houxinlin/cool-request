package com.hxl.plugin.springboot.invoke.script;
import com.hxl.plugin.springboot.invoke.script.ILog;
import com.hxl.plugin.springboot.invoke.script.Utils;
public class ResponseApi {
    private Response response;
    private ILog log;
    /**
     * do not delete
     *
     * @param log     log print
     * @param response response context
     */
    public ResponseApi(ILog log, Response response) {
        this.response = response;
        this.log = log;
    }
    public void handlerResponse() {

    }
}