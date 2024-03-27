/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ViewRegister.java is part of Cool Request
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

package com.cool.request.view;

import com.cool.request.view.tool.Provider;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

@Service
public final class ViewRegister implements Provider {
    public static ViewRegister getInstance(Project project) {
        return project.getService(ViewRegister.class);
    }

    public Map<Class<?>, View> viewMap = new HashMap<>();

    public <T extends View> void registerView(View view) {
        viewMap.put(view.getClass(), view);
    }

    public <T> T getView(Class<T> viewClass) {
        View view = viewMap.get(viewClass);
        if (view == null) return null;
        if (viewClass.isInstance(view)) return ((T) view);
        return null;
    }
}
