/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * KeyValueColumnModeFactory.java is part of Cool Request
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

import com.cool.request.view.page.cell.DefaultTextCellRenderer;
import com.intellij.ui.table.JBTable;

import java.util.List;

public class KeyValueTableModeFactory implements TableModeFactory {
    @Override
    public List<Column> createColumn(JBTable table) {
        return List.of(
                new ColumnImpl("", table.getDefaultRenderer(Boolean.class), 30),
                new ColumnImpl("Key", new DefaultTextCellRenderer()),
                new ColumnImpl("Value", new DefaultTextCellRenderer()),
                new ColumnImpl("", new TableCellAction.TableDeleteButtonRenderer(),80));
    }

    @Override
    public Object[] createNewEmptyRow() {
        return new Object[]{Boolean.FALSE, "", "", ""};
    }

    @Override
    public ToolbarDecoratorFactory createToolbarDecoratorFactory(TablePanel tablePanel) {
        return new DefaultToolbarDecoratorFactory(tablePanel, this);
    }
}
