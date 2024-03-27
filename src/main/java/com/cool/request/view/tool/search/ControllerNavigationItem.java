/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ControllerNavigationItem.java is part of Cool Request
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

package com.cool.request.view.tool.search;

import com.cool.request.components.http.Controller;
import com.cool.request.utils.HttpMethodIconUtils;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ControllerNavigationItem extends Controller implements NavigationItem {
    private Project project;

    public ControllerNavigationItem(Controller controller, Project project) {
        setMethodName(controller.getMethodName());
        setUrl(controller.getUrl());
        setContextPath(controller.getContextPath());
        setServerPort(controller.getServerPort());
        setHttpMethod(controller.getHttpMethod());
        setId(controller.getId());
        setSimpleClassName(controller.getSimpleClassName());
        setModuleName(controller.getModuleName());
        setOwnerPsiMethod(controller.getOwnerPsiMethod());
        this.project = project;
    }

    @Override
    public @Nullable String getName() {
        return getUrl();
    }

    @Override
    public @Nullable ItemPresentation getPresentation() {
        return new ColoredItemPresentation() {
            @Override
            public @Nullable TextAttributesKey getTextAttributesKey() {
                return TextAttributesKey.find(getUrl());
            }

            @Override
            public @Nullable String getPresentableText() {
                return getUrl();
            }

            @Override
            public @Nullable String getLocationString() {
                return getServerPort() + ":" + getSimpleClassName() + "." + getMethodName();
            }

            @Override
            public @Nullable Icon getIcon(boolean unused) {
                return HttpMethodIconUtils.getIconByHttpMethod(getHttpMethod());
            }
        };
    }

    @Override
    public void navigate(boolean requestFocus) {
        goToCode(project);
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }
}
