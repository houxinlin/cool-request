/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * XxlJobInvokeEndpoint.java is part of Cool Request
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

public class XxlJobInvokeEndpoint  extends SpringInvokeEndpoint{
    private String className;
    private String methodName;
    private String springInnerId;

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

    public String getSpringInnerId() {
        return springInnerId;
    }

    public void setSpringInnerId(String springInnerId) {
        this.springInnerId = springInnerId;
    }

    public static final class XxlJobInvokeEndpointBuilder {
        private String id;
        private String className;
        private String methodName;
        private String springInnerId;

        private XxlJobInvokeEndpointBuilder() {
        }

        public static XxlJobInvokeEndpointBuilder aXxlJobInvokeEndpoint() {
            return new XxlJobInvokeEndpointBuilder();
        }

        public XxlJobInvokeEndpointBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public XxlJobInvokeEndpointBuilder withClassName(String className) {
            this.className = className;
            return this;
        }

        public XxlJobInvokeEndpointBuilder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public XxlJobInvokeEndpointBuilder withSpringInnerId(String springInnerId) {
            this.springInnerId = springInnerId;
            return this;
        }

        public XxlJobInvokeEndpoint build() {
            XxlJobInvokeEndpoint xxlJobInvokeEndpoint = new XxlJobInvokeEndpoint();
            xxlJobInvokeEndpoint.setId(id);
            xxlJobInvokeEndpoint.setClassName(className);
            xxlJobInvokeEndpoint.setMethodName(methodName);
            xxlJobInvokeEndpoint.setSpringInnerId(springInnerId);
            return xxlJobInvokeEndpoint;
        }
    }
}
