/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FloatWindowsAnAction.java is part of Cool Request
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

import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import org.jetbrains.annotations.NotNull;

import static com.cool.request.common.constant.CoolRequestConfigConstant.PLUGIN_ID;

public class FloatWindowsAnAction extends DynamicAnAction {
    public FloatWindowsAnAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("float.windows"),
                () -> ResourceBundleUtils.getString("float.windows"), KotlinCoolRequestIcons.INSTANCE.getWINDOW());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        if (toolWindow == null) {
            return;
        }
        if (!toolWindow.isActive()) {
            toolWindow.activate(null);
        }
        if (toolWindow.getType() == ToolWindowType.DOCKED) {
            toolWindow.setType(ToolWindowType.FLOATING, () -> {
            });
            return;
        }
        toolWindow.setType(ToolWindowType.DOCKED, () -> {
        });
    }
}
