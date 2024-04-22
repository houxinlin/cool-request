/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringScheduled.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.components.scheduled;

import com.cool.request.common.bean.components.StaticComponent;
import com.cool.request.components.CanMark;
import com.cool.request.components.ComponentType;
import com.cool.request.utils.ComponentIdUtils;
import com.intellij.openapi.project.Project;

import java.io.Serializable;

public class SpringScheduled extends BasicScheduled
        implements CanMark, Serializable, StaticComponent {
    private static final long serialVersionUID = 1000000000;

    @Override
    public ComponentType getComponentType() {
        return ComponentType.SCHEDULE;
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
