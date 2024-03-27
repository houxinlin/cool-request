/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DynamicIconToggleActionButton.java is part of Cool Request
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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.ui.AnActionButton;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Supplier;

public abstract class DynamicIconToggleActionButton extends AnActionButton implements Toggleable {
    private Function0<Icon> icon;

    public DynamicIconToggleActionButton(Supplier<String> text, Function0<Icon> icon) {
        super(text, Presentation.NULL_STRING, icon.invoke());
        this.icon = icon;
    }

    public DynamicIconToggleActionButton(@NotNull Supplier<String> text, Icon icon) {
        super(text, Presentation.NULL_STRING, icon);
    }

    /**
     * Returns the selected (checked, pressed) state of the action.
     *
     * @param e the action event representing the place and context in which the selected state is queried.
     * @return true if the action is selected, false otherwise
     */
    public abstract boolean isSelected(AnActionEvent e);

    /**
     * Sets the selected state of the action to the specified value.
     *
     * @param e     the action event which caused the state change.
     * @param state the new selected state of the action.
     */
    public abstract void setSelected(AnActionEvent e, boolean state);

    @Override
    public final void actionPerformed(@NotNull AnActionEvent e) {
        final boolean state = !isSelected(e);
        setSelected(e, state);
        final Presentation presentation = e.getPresentation();
        Toggleable.setSelected(presentation, state);
    }

    @Override
    public void updateButton(@NotNull AnActionEvent e) {
        final boolean selected = isSelected(e);
        final Presentation presentation = e.getPresentation();
        presentation.setIcon(icon.invoke());
        Toggleable.setSelected(presentation, selected);

    }
}
