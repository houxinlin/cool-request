/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BasicScheduled.java is part of Cool Request
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

import com.cool.request.common.bean.components.BasicComponent;
import com.cool.request.components.ComponentType;
import com.cool.request.components.JavaClassComponent;
import com.cool.request.utils.ComponentIdUtils;
import com.cool.request.utils.NavigationUtils;
import com.intellij.openapi.project.Project;

import java.io.Serializable;

public abstract class BasicScheduled extends BasicComponent implements JavaClassComponent, Serializable {
    private static final long serialVersionUID = 1000000000;
    private String moduleName;
    private int serverPort;
    private String className;
    private String methodName;

    @Override
    public void calcId(Project project) {
        setId(ComponentIdUtils.getMd5(project, this));
    }

    @Override
    public void goToCode(Project project) {
        NavigationUtils.jumpToSpringScheduledMethod(project, this);
    }

    @Override
    public String getJavaClassName() {
        return className;
    }

    @Override
    public String getUserProjectModuleName() {
        return moduleName;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
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
}
