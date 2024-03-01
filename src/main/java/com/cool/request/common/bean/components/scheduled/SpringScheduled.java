package com.cool.request.common.bean.components.scheduled;

import com.cool.request.component.CanMark;
import com.cool.request.utils.ComponentIdUtils;
import com.intellij.openapi.project.Project;

public class SpringScheduled extends BasicScheduled
        implements CanMark {

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
