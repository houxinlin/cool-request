/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DynamicUrlAnAction.java is part of Cool Request
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

package com.cool.request.action.actions;

import com.cool.request.utils.WebBrowseUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DynamicUrlAnAction extends AnAction {
    private final String url;

    /**
     * DynamicUrlAnAction is a class that extends AnAction.
     * @param title The text to be displayed as a tooltip for the component, when the component is visible.
     * @param icon The icon to display in the component.
     * @param url The url to open.
     */
    public DynamicUrlAnAction(String title, Icon icon, String url) {
        super(() -> title, icon);
        this.url = url;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        WebBrowseUtils.browse(this.url);
    }
}
