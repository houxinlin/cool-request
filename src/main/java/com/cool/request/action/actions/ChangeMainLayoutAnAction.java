/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ChangeMainLayoutAnAction.java is part of Cool Request
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

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ChangeMainLayoutAnAction extends BaseAnAction {
    private final Project project;

    /**
     * change layout.
     *
     * @param project The project in which the action is being created.
     */
    public ChangeMainLayoutAnAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("change.layout"),
                () -> ResourceBundleUtils.getString("change.layout"), CoolRequestIcons.LAYOUT);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.CHANGE_LAYOUT).event();

    }
}
