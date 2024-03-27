/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * Controller.java is part of Cool Request
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

package com.cool.request.components.http;

import com.cool.request.common.bean.components.BasicComponent;
import com.cool.request.components.ComponentType;
import com.cool.request.components.JavaClassComponent;
import com.cool.request.utils.ComponentIdUtils;
import com.cool.request.utils.NavigationUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Controller extends BasicComponent implements JavaClassComponent, Serializable {
    private static final long serialVersionUID = 1000000000;
    private String moduleName;
    private String contextPath;
    private int serverPort;
    private String url;
    private String simpleClassName;
    private String methodName;
    private String httpMethod;
    private List<String> paramClassList;

    private transient PsiClass superPsiClass; //一些http方法定义在接口中
    private transient List<PsiMethod> ownerPsiMethod = new ArrayList<>();

    @Override
    public void calcId(Project project) {
        setId(ComponentIdUtils.getMd5(project, this));
    }

    @Override
    public void goToCode(Project project) {
        if (StringUtils.isEmpty(this.getModuleName())) return;
        NavigationUtils.jumpToControllerMethod(project, this);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.CONTROLLER;
    }

    public List<PsiMethod> getOwnerPsiMethod() {
        return ownerPsiMethod;
    }

    public void setOwnerPsiMethod(List<PsiMethod> ownerPsiMethod) {
        this.ownerPsiMethod = ownerPsiMethod;
    }

    public PsiClass getSuperPsiClass() {
        return superPsiClass;
    }

    public void setSuperPsiClass(PsiClass superPsiClass) {
        this.superPsiClass = superPsiClass;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
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

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<String> getParamClassList() {
        return paramClassList;
    }

    public void setParamClassList(List<String> paramClassList) {
        this.paramClassList = paramClassList;
    }


    @Override
    public String getJavaClassName() {
        return this.simpleClassName;
    }

    @Override
    public String getUserProjectModuleName() {
        return this.moduleName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Controller that = (Controller) object;
        return serverPort == that.serverPort && Objects.equals(moduleName, that.moduleName) && Objects.equals(contextPath, that.contextPath) && Objects.equals(url, that.url) && Objects.equals(simpleClassName, that.simpleClassName) && Objects.equals(methodName, that.methodName) && Objects.equals(httpMethod, that.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, contextPath, serverPort, url, simpleClassName, methodName, httpMethod);
    }

    public static final class ControllerBuilder {
        private String moduleName;
        private String contextPath;
        private int serverPort;
        private String url;
        private String simpleClassName;
        private String methodName;
        private String httpMethod;
        private List<String> paramClassList;

        private ControllerBuilder() {
        }

        public static ControllerBuilder aController() {
            return new ControllerBuilder();
        }

        public ControllerBuilder withModuleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public ControllerBuilder withContextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        public ControllerBuilder withServerPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public ControllerBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public ControllerBuilder withSimpleClassName(String simpleClassName) {
            this.simpleClassName = simpleClassName;
            return this;
        }

        public ControllerBuilder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public ControllerBuilder withHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public ControllerBuilder withParamClassList(List<String> paramClassList) {
            this.paramClassList = paramClassList;
            return this;
        }

        public Controller build(Controller controller, Project project) {
            controller.setModuleName(moduleName);
            controller.setContextPath(contextPath);
            controller.setServerPort(serverPort);
            controller.setUrl(url);
            controller.setSimpleClassName(simpleClassName);
            controller.setMethodName(methodName);
            controller.setHttpMethod(httpMethod);
            controller.setParamClassList(paramClassList);
            controller.setId(ComponentIdUtils.getMd5(project, controller));
            return controller;
        }
    }
}
