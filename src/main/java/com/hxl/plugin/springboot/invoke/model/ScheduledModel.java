package com.hxl.plugin.springboot.invoke.model;
import java.util.List;
public class ScheduledModel  extends Model{
    public List<SpringScheduledInvokeBean> scheduledInvokeBeans;
    public int port;

    public ScheduledModel(List<SpringScheduledInvokeBean> scheduledInvokeBeans, int port) {
        this.scheduledInvokeBeans = scheduledInvokeBeans;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ScheduledModel(List<SpringScheduledInvokeBean> scheduledInvokeBeans) {
        this.scheduledInvokeBeans = scheduledInvokeBeans;
    }

    public ScheduledModel() {
    }

    public List<SpringScheduledInvokeBean> getScheduledInvokeBeans() {
        return scheduledInvokeBeans;
    }

    public void setScheduledInvokeBeans(List<SpringScheduledInvokeBean> scheduledInvokeBeans) {
        this.scheduledInvokeBeans = scheduledInvokeBeans;
    }
}
