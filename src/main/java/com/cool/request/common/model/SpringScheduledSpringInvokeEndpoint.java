/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringScheduledSpringInvokeEndpoint.java is part of Cool Request
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

package com.cool.request.common.model;

public class SpringScheduledSpringInvokeEndpoint extends SpringInvokeEndpoint {
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

        public SpringScheduledSpringInvokeEndpoint build() {
            SpringScheduledSpringInvokeEndpoint scheduledInvokeBean = new SpringScheduledSpringInvokeEndpoint();
            scheduledInvokeBean.setId(id);
            scheduledInvokeBean.setClassName(className);
            scheduledInvokeBean.setMethodName(methodName);
            return scheduledInvokeBean;
        }
    }
}
