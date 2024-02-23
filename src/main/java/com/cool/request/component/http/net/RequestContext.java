package com.cool.request.component.http.net;

import com.cool.request.common.bean.components.controller.Controller;

public class RequestContext {
    private String id;
    private final StringBuffer log = new StringBuffer();
    private Controller controller;
    public RequestContext(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void appendLog(String log) {
        this.log.append(log);
    }

    public String getLog() {
        return this.log.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void clear() {
        log.delete(0, log.length());
    }


}
