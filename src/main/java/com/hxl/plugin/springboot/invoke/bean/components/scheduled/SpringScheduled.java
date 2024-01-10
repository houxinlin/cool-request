package com.hxl.plugin.springboot.invoke.bean.components.scheduled;

import com.hxl.plugin.springboot.invoke.bean.components.BasicComponent;
import com.hxl.plugin.springboot.invoke.utils.ComponentIdUtils;
import com.intellij.openapi.project.Project;

public class SpringScheduled extends BasicComponent {
    private String moduleName;
    private int serverPort;
    private String className;
    private String methodName;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

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


    public static final class SpringScheduledBuilder {
        private String id;
        private String moduleName;
        private int serverPort;
        private String simpleClassName;
        private String methodName;

        private SpringScheduledBuilder() {
        }

        public static SpringScheduledBuilder aSpringScheduled() {
            return new SpringScheduledBuilder();
        }

        public SpringScheduledBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public SpringScheduledBuilder withModuleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public SpringScheduledBuilder withServerPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public SpringScheduledBuilder withSimpleClassName(String simpleClassName) {
            this.simpleClassName = simpleClassName;
            return this;
        }

        public SpringScheduledBuilder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public SpringScheduled build(Project project, SpringScheduled springScheduled) {
            springScheduled.setModuleName(moduleName);
            springScheduled.setServerPort(serverPort);
            springScheduled.setClassName(simpleClassName);
            springScheduled.setMethodName(methodName);
            springScheduled.setId(ComponentIdUtils.getMd5(project, springScheduled));
            return springScheduled;
        }
    }
}
