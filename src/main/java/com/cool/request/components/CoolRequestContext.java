/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestContext.java is part of Cool Request
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

package com.cool.request.components;

import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service
public final class CoolRequestContext {
    private MainBottomHTTPContainer mainBottomHTTPContainer;
    private IRequestParamManager mainRequestParamManager;

    public void setMainRequestParamManager(IRequestParamManager mainRequestParamManager) {
        this.mainRequestParamManager = mainRequestParamManager;
    }

    public static CoolRequestContext getInstance(Project project) {
        return project.getService(CoolRequestContext.class);
    }

    @NotNull
    public IRequestParamManager getMainRequestParamManager() {
        return mainBottomHTTPContainer.getMainBottomHttpInvokeViewPanel().getHttpRequestParamPanel();
    }

    public void setMainBottomHTTPContainer(@NotNull MainBottomHTTPContainer mainBottomHTTPContainer) {
        this.mainBottomHTTPContainer = mainBottomHTTPContainer;
    }

    @NotNull
    public MainBottomHTTPContainer getMainBottomHTTPContainer() {
        return mainBottomHTTPContainer;
    }
}
