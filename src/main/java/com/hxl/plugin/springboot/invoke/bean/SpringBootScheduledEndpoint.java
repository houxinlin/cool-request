package com.hxl.plugin.springboot.invoke.bean;

import com.hxl.plugin.springboot.invoke.model.InvokeBean;

import java.util.Objects;

public class SpringBootScheduledEndpoint extends InvokeBean {
    private final String type="scheduled";
    private String className;
    private String methodName;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpringBootScheduledEndpoint)) return false;
        SpringBootScheduledEndpoint that = (SpringBootScheduledEndpoint) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String getType() {
        return type;
    }

    public static final class ScheduledInvokeBeanBuilder {
        private String id;
        private String className;
        private String methodName;

        private ScheduledInvokeBeanBuilder() {
        }

        public static ScheduledInvokeBeanBuilder aScheduledInvokeBean() {
            return new ScheduledInvokeBeanBuilder();
        }

        public ScheduledInvokeBeanBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ScheduledInvokeBeanBuilder withClassName(String className) {
            this.className = className;
            return this;
        }

        public ScheduledInvokeBeanBuilder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public SpringBootScheduledEndpoint build() {
            SpringBootScheduledEndpoint springBootScheduledEndpoint = new SpringBootScheduledEndpoint();
            springBootScheduledEndpoint.setId(id);
            springBootScheduledEndpoint.setClassName(className);
            springBootScheduledEndpoint.setMethodName(methodName);
            return springBootScheduledEndpoint;
        }
    }
}
