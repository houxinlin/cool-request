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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultToolbarDecoratorFactory implements ToolbarDecoratorFactory {
    private final TableOperator tableOperator;
    private final TableModeFactory<?> tableModeFactory;

    public DefaultToolbarDecoratorFactory(TableOperator tableOperator, TableModeFactory<?> tableModeFactory) {
        this.tableOperator = tableOperator;
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
                tableOperator.addNewRow(tableModeFactory.createNewEmptyRow());
            }
        }, new DynamicAnActionButton(() -> "Remove", KotlinCoolRequestIcons.INSTANCE.getSUBTRACTION()) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                tableOperator.stopEditor();
                List<TableDataRow> tableDataRows = tableOperator.listTableData(tableDataRow -> tableDataRow.getData()[0].equals(Boolean.TRUE));
                for (int i = tableDataRows.size() - 1; i >= 0; i--) {
                    tableOperator.removeRow(tableDataRows.get(i).getRowIndex());
                }
            }
        }, new DynamicAnActionButton(() -> "Copy", KotlinCoolRequestIcons.INSTANCE.getCOPY()) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                tableOperator.stopEditor();
                Object[] selectData = tableOperator.getSelectData();
                if (selectData != null) tableOperator.addNewRow(selectData);
            }
        });
    }
}
