/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringBootScheduledEndpoint.java is part of Cool Request
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

package com.cool.request.common.bean;

import com.cool.request.common.model.SpringInvokeEndpoint;

import java.util.Objects;

public class SpringBootScheduledEndpoint extends SpringInvokeEndpoint {
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
