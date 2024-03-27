/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DynamicAnAction.java is part of Cool Request
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
import com.cool.request.components.CoolRequestPluginDisposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Supplier;

public abstract class DynamicAnAction extends AnAction {
    private final Project project;

    private Function0<Icon> icon;

    /**
     * Constructs a new BaseAnAction object.
     *
     * @param project     The project in which the action is being created.
     * @param title       A supplier function that provides the title of the action.
     * @param description A supplier function that provides the description of the action.
     * @param icon        The icon to be used for the action.
     */
    public DynamicAnAction(Project project, Supplier<String> title, Supplier<String> description, Function0<Icon> icon) {
        super(title.get(), description.get(), icon.invoke());
        this.project = project;
        this.icon = icon;
        MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();

        connect.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, (CoolRequestIdeaTopic.BaseListener) () -> {
            getTemplatePresentation().setText(title.get());
            getTemplatePresentation().setDescription(description.get());
        });
        if (project == null) {
            return;
        }
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), connect);
    }

    public DynamicAnAction(Project project, Supplier<String> title, Function0<Icon> icon) {
        this(project, title, title, icon);

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setIcon(icon.invoke());
    }

    public Project getProject() {
        return project;
    }
}
