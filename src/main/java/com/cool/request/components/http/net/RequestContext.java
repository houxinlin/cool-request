package com.cool.request.components.http.net;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.script.ScriptExecute;
import com.cool.request.view.main.HTTPEventListener;

import java.util.List;
import java.util.Objects;

public class RequestContext {
    private String id; //请求id
    private final StringBuffer log = new StringBuffer();//
    private Controller controller;
    private long beginTimeMillis;
    private ScriptExecute scriptExecute;

    public RequestContext(Controller controller) {
        this.controller = controller;
        this.id = controller.getId();
        this.beginTimeMillis = System.currentTimeMillis();
    }

    public void beginSend(RequestContext requestContext) {
        for (HTTPEventListener httpEventListener : getHttpEventListeners()) {
            httpEventListener.beginSend(requestContext);
        }
    }

    public ScriptExecute getScriptExecute() {
        return scriptExecute;
    }

    public void setScriptExecute(ScriptExecute scriptExecute) {
        this.scriptExecute = scriptExecute;
    }

    private List<HTTPEventListener> httpEventListeners;

    public List<HTTPEventListener> getHttpEventListeners() {
        return httpEventListeners;
    }

    public void setHttpEventListeners(List<HTTPEventListener> httpEventListeners) {
        this.httpEventListeners = httpEventListeners;
    }

    public long getBeginTimeMillis() {
        return beginTimeMillis;
    }

    public void setBeginTimeMillis(long beginTimeMillis) {
        this.beginTimeMillis = beginTimeMillis;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RequestContext that = (RequestContext) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
