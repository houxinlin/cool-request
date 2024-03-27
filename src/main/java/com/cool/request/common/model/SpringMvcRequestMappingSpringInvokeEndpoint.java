/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringMvcRequestMappingSpringInvokeEndpoint.java is part of Cool Request
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

import java.util.List;
import java.util.Objects;

public class SpringMvcRequestMappingSpringInvokeEndpoint extends SpringInvokeEndpoint {
    private String url;
    private String simpleClassName;
    private String methodName;
    private String httpMethod;
    private List<String> paramClassList;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpringMvcRequestMappingSpringInvokeEndpoint)) {
            return false;
        }
        SpringMvcRequestMappingSpringInvokeEndpoint that = (SpringMvcRequestMappingSpringInvokeEndpoint) o;
        return Objects.equals(getUrl(), that.getUrl()) && Objects.equals(getSimpleClassName(), that.getSimpleClassName()) && Objects.equals(getMethodName(), that.getMethodName()) && Objects.equals(getHttpMethod(), that.getHttpMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getSimpleClassName(), getMethodName(), getHttpMethod());
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParamClassList() {
        return paramClassList;
    }

    public void setParamClassList(List<String> paramClassList) {
        this.paramClassList = paramClassList;
    }

    public static final class RequestMappingInvokeBeanBuilder {
        private String id;
        private String url;
        private String simpleClassName;
        private String methodName;
        private String httpMethod;

        private RequestMappingInvokeBeanBuilder() {
        }

        public static RequestMappingInvokeBeanBuilder aRequestMappingInvokeBean() {
            return new RequestMappingInvokeBeanBuilder();
        }

        public RequestMappingInvokeBeanBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public RequestMappingInvokeBeanBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public RequestMappingInvokeBeanBuilder withSimpleClassName(String simpleClassName) {
            this.simpleClassName = simpleClassName;
            return this;
        }

        public RequestMappingInvokeBeanBuilder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public RequestMappingInvokeBeanBuilder withHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public SpringMvcRequestMappingSpringInvokeEndpoint build() {
            SpringMvcRequestMappingSpringInvokeEndpoint SpringMvcRequestMappingSpringInvokeEndpoint = new SpringMvcRequestMappingSpringInvokeEndpoint();
            SpringMvcRequestMappingSpringInvokeEndpoint.setId(id);
            SpringMvcRequestMappingSpringInvokeEndpoint.setUrl(url);
            SpringMvcRequestMappingSpringInvokeEndpoint.setSimpleClassName(simpleClassName);
            SpringMvcRequestMappingSpringInvokeEndpoint.setMethodName(methodName);
            SpringMvcRequestMappingSpringInvokeEndpoint.setHttpMethod(httpMethod);
            return SpringMvcRequestMappingSpringInvokeEndpoint;
        }
    }
}
