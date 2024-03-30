/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StaticRefreshAction.java is part of Cool Request
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
import com.cool.request.scan.CoolRequestScan;
import com.cool.request.utils.NavigationUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;


public class StaticRefreshAction extends AnAction {
    private final Project project;
    private final AtomicBoolean refreshAtomicBoolean = new AtomicBoolean();

    public StaticRefreshAction(Project project) {
        super("Static Refresh", "Static refresh", CoolRequestIcons.SCAN);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //先删除所有数据
        if (refreshAtomicBoolean.get()) return;
        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.DELETE_ALL_DATA).onDelete();
        ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER).event();
        refreshAtomicBoolean.set(true);
        CoolRequestScan.staticScan(project, () -> refreshAtomicBoolean.set(false));
    }
}
