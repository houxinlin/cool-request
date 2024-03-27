/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BaseToggleAction.java is part of Cool Request
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

package com.cool.request.action.response;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BaseToggleAction extends ToggleAction {
    private final ToggleManager toggleManager;
    private final String text;
    public BaseToggleAction(String text, Icon icon, ToggleManager toggleManager) {
        super(text, text, icon);
        this.toggleManager = toggleManager;
        this.text = text;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return toggleManager.isSelected(this.text);
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        toggleManager.setSelect(this.text);
    }
}
