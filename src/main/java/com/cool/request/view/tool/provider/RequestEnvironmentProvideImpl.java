/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestEnvironmentProvideImpl.java is part of Cool Request
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

package com.cool.request.view.tool.provider;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service
public final class RequestEnvironmentProvideImpl implements RequestEnvironmentProvide {
    private final Project project;

    public static RequestEnvironmentProvideImpl getInstance(Project project) {
        return project.getService(RequestEnvironmentProvideImpl.class);
    }

    public RequestEnvironmentProvideImpl(Project project) {
        this.project = project;
    }

    @Override
    public @NotNull RequestEnvironment getSelectRequestEnvironment() {
        CoolRequestEnvironmentPersistentComponent.State state = CoolRequestEnvironmentPersistentComponent.getInstance(project);
        if (StringUtils.isEmpty(state.getSelectId())) return new EmptyEnvironment();

        for (RequestEnvironment environment : state.getEnvironments()) {
            if (StringUtils.isEqualsIgnoreCase(state.getSelectId(), environment.getId())) return environment;
        }
        return new EmptyEnvironment();
    }


}
