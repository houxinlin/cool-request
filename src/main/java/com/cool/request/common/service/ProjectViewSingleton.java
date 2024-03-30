/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ProjectViewSingleton.java is part of Cool Request
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

package com.cool.request.common.service;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.components.CoolRequestContext;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.view.component.CoolRequestView;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;

@Service
public final class ProjectViewSingleton {
    private MainBottomHTTPContainer mainBottomHTTPContainer;

    private CoolRequestView coolRequestView;
    private Project project;

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectViewSingleton(Project project) {
        this.project = project;
    }

    public static ProjectViewSingleton getInstance(Project project) {
        return project.getService(ProjectViewSingleton.class);
    }

    public CoolRequestView createAndCoolRequestView() {
        if (coolRequestView == null) coolRequestView = new CoolRequestView(project);
        return coolRequestView;
    }

    public MainBottomHTTPContainer createAndGetMainBottomHTTPContainer() {
        if (mainBottomHTTPContainer == null) {
            mainBottomHTTPContainer = new MainBottomHTTPContainer(project);
            Disposer.register(CoolRequestPluginDisposable.getInstance(project), mainBottomHTTPContainer);
            ProviderManager.registerProvider(MainBottomHTTPContainer.class, CoolRequestConfigConstant.MainBottomHTTPContainerKey, mainBottomHTTPContainer, project);
            CoolRequestContext coolRequestContext = CoolRequestContext.getInstance(project);
            coolRequestContext.setMainBottomHTTPContainer(mainBottomHTTPContainer);
            coolRequestContext.setMainRequestParamManager(mainBottomHTTPContainer.getMainBottomHttpInvokeViewPanel().getHttpRequestParamPanel());
            mainBottomHTTPContainer.invalidate();
            mainBottomHTTPContainer.updateUI();
            mainBottomHTTPContainer.revalidate();
        }
        return mainBottomHTTPContainer;
    }
}
