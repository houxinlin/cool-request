package com.hxl.plugin.springboot.invoke.net;

public class RequestContext {
    private String id;
    private final StringBuffer log = new StringBuffer();

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


}
