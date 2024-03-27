/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ShowMarkNodeAnAction.java is part of Cool Request
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

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;

public class ShowMarkNodeAnAction extends DynamicIconToggleActionButton {
    private final MakeSelectedListener makeSelectedListener;
    private boolean isSelected = false;

    public ShowMarkNodeAnAction(MakeSelectedListener makeSelectedListener) {
        super(()-> ResourceBundleUtils.getString("mark"), KotlinCoolRequestIcons.INSTANCE.getMARK());
        this.makeSelectedListener = makeSelectedListener;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return isSelected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        isSelected = !isSelected;
        makeSelectedListener.setMarkSelected(e, state);
    }

    public static interface MakeSelectedListener {
        public void setMarkSelected(@NotNull AnActionEvent e, boolean state);
    }
}
