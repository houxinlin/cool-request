/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DefaultToolbarDecoratorFactory.java is part of Cool Request
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

package com.cool.request.view.table;

import com.cool.request.action.actions.DynamicAnActionButton;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.border.CustomLineBorder;
import org.jetbrains.annotations.NotNull;

import javax.swing.border.Border;
import java.util.List;

public class DefaultToolbarDecoratorFactory implements ToolbarDecoratorFactory {
    private final TablePanel tablePanel;
    private TableModeFactory tableModeFactory;

    public DefaultToolbarDecoratorFactory(TablePanel tablePanel, TableModeFactory tableModeFactory) {
        this.tablePanel = tablePanel;
        this.tableModeFactory = tableModeFactory;
    }

    @Override
    public Border getToolbarBorder() {
        return new CustomLineBorder(0, 0, 0, 0);
    }

    @Override
    public Border getPanelBorder() {
        return new CustomLineBorder(0, 0, 0, 0);
    }

    @Override
    public List<AnActionButton> getExtraActions() {
        return List.of(new DynamicAnActionButton(() -> "Add", KotlinCoolRequestIcons.INSTANCE.getADD()) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                tablePanel.addNewEmptyRow(tableModeFactory.createNewEmptyRow());
            }
        }, new DynamicAnActionButton(() -> "Remove", KotlinCoolRequestIcons.INSTANCE.getSUBTRACTION()) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

            }
        }, new DynamicAnActionButton(() -> "Copy", KotlinCoolRequestIcons.INSTANCE.getCOPY()) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

            }
        });
    }
}
