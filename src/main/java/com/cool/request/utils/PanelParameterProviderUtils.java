/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * PanelParameterProviderUtils.java is part of Cool Request
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

package com.cool.request.utils;

import com.cool.request.components.http.Controller;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.components.CoolRequestContext;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.param.CacheParameterProvider;
import com.cool.request.utils.param.GuessParameterProvider;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.project.Project;

public class PanelParameterProviderUtils {
    public static HTTPParameterProvider getPanelParameterProvider(Project project, Controller controller) {
        IRequestParamManager requestParamManager = CoolRequestContext.getInstance(project)
                .getMainBottomHTTPContainer()
                .getMainBottomHttpInvokeViewPanel()
                .getHttpRequestParamPanel();
        if (requestParamManager.isAvailable()) {
            if (StringUtils.isEqualsIgnoreCase(requestParamManager.getCurrentController().getId(), controller.getId())) {
                return new PanelParameterProvider(requestParamManager);
            }
        }
        RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (cache != null) {
            return new CacheParameterProvider();
        }
        return new GuessParameterProvider();
    }
}
