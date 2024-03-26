package com.cool.request.components.http.net;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.script.ScriptExecute;
import com.cool.request.view.main.HTTPEventListener;
import com.cool.request.view.main.HTTPEventOrder;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RequestContext {
    private String id; //请求id
    private final StringBuffer log = new StringBuffer();//
    private Controller controller;
    private long beginTimeMillis;
    private ScriptExecute scriptExecute;
    private Project project;

    public RequestContext(Controller controller, Project project) {
        this.controller = controller;
        this.project = project;
        this.id = controller.getId();
        this.beginTimeMillis = System.currentTimeMillis();
    }

    public void beginSend(ProgressIndicator progressIndicator) {
        for (HTTPEventListener httpEventListener : getHttpEventListeners()) {
            httpEventListener.beginSend(this, progressIndicator);
        }
    }

    public void endSend(HTTPResponseBody httpResponseBody) {
        List<HTTPEventListener> httpEventListeners = getHttpEventListeners();
        httpEventListeners.sort((o1, o2) -> {
            int order1 = getOrderValue(o1);
            int order2 = getOrderValue(o2);
            return Integer.compare(order1, order2);
        });
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Handler response") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                for (HTTPEventListener httpEventListener : httpEventListeners) {
                    httpEventListener.endSend(RequestContext.this, httpResponseBody, indicator);
                }
            }
        });

    }

    private int getOrderValue(HTTPEventListener listener) {
        HTTPEventOrder annotation = listener.getClass().getAnnotation(HTTPEventOrder.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return HTTPEventOrder.MIN;
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
