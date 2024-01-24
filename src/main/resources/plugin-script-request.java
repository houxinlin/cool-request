package cool.request.script;

public class CoolRequestScript {
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