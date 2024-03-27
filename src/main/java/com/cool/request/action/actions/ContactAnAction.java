/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ContactAnAction.java is part of Cool Request
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
import com.cool.request.view.dialog.ContactDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


public class ContactAnAction extends DynamicAnAction {
    /**
     * ContactAnAction is a class that extends BaseAnAction.
     *
     * @param project The project in which the action is being created.
     */
    public ContactAnAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("author"),
                () -> ResourceBundleUtils.getString("author"),
                KotlinCoolRequestIcons.INSTANCE.getCHAT());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new ContactDialog(getProject()).show();
    }
}
