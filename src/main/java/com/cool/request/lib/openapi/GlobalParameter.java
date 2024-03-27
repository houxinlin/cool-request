/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * GlobalParameter.java is part of Cool Request
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

package com.cool.request.lib.openapi;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlobalParameter {
    public static List<KeyValue> getGlobalHeader(Project project) {
        RequestEnvironment selectRequestEnvironment = RequestEnvironmentProvideImpl.getInstance(project).getSelectRequestEnvironment();
        if (selectRequestEnvironment instanceof EmptyEnvironment) return new ArrayList<>();

        return selectRequestEnvironment.getHeader();
    }

    public static List<FormDataInfo> getGlobalFormData(Project project) {
        RequestEnvironment selectRequestEnvironment = RequestEnvironmentProvideImpl.getInstance(project).getSelectRequestEnvironment();
        if (selectRequestEnvironment instanceof EmptyEnvironment) return new ArrayList<>();

        return selectRequestEnvironment.getFormData();
    }
}
