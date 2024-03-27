package com.cool.request.script;
import com.cool.request.script.HTTPRequest;
import com.cool.request.script.ILog;
class CoolRequestScript {
    private ILog log;

    /**
     * 1.Here, you can freely use any Java syntax, classes in any dependency, including third-party and user project.
     * 2.modifying class and method names or parameters is not allowed
     * 3.If false is returned, the HTTP request will be cancel
     *
     * @param log     Log output
     * @param request request parameters, where parameters can be changed
     */
    public boolean handlerRequest(ILog log, HTTPRequest request,IEnv env) {
        this.log = log;
        /**
         * Write your code here
         */

        return true;
    }
    private void println(Object object) {
        if (object == null) return;
        if (log == null) return;
        log.print(object.toString());

    }

}