/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DynamicAnActionButton.java is part of Cool Request
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
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.AnActionButton;
import com.intellij.util.messages.MessageBusConnection;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Supplier;

public abstract class DynamicAnActionButton extends AnActionButton {
    private final Function0<Icon> icon;

    public DynamicAnActionButton(Supplier<String> title, Function0<Icon> icon) {
        super(title, title, icon.invoke());
        this.icon = icon;
        MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();
        connect.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, () -> {
            getTemplatePresentation().setText(title.get());
            getTemplatePresentation().setDescription(title.get());
        });
    }

    @Override
    public void updateButton(@NotNull AnActionEvent e) {
        super.updateButton(e);
        e.getPresentation().setIcon(icon.invoke());
    }
}
