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

import com.cool.request.components.http.KeyValue;
import com.cool.request.view.page.cell.DefaultTextCellEditable;
import com.cool.request.view.page.cell.DefaultTextCellRenderer;
import com.cool.request.view.widget.AutocompleteField;

import java.util.List;
import java.util.stream.Collectors;

import static com.cool.request.utils.Constant.EMPTY_STRING;

public class KeyValueTableModeFactory implements TableModeFactory<KeyValue> {
    private final AutocompleteField keyAutoComplete;
    private final AutocompleteField valueAutoComplete;
    private final SuggestFactory suggestFactory;

    public KeyValueTableModeFactory(SuggestFactory suggestFactory) {
        this.suggestFactory = suggestFactory;
        keyAutoComplete = new AutocompleteField(suggestFactory.createSuggestLookup(), suggestFactory.getWindow());
        valueAutoComplete = new AutocompleteField(null, suggestFactory.getWindow());
    }

    @Override
    public List<Column> createColumn(TableOperator table) {
        table.registerTableDataChangeEvent(1, (row, col) ->
                valueAutoComplete.setLookup(target -> suggestFactory.getValueSuggest(table.getValueAt(row, 1).toString())
                        .stream().filter(s -> s.startsWith(target)).collect(Collectors.toList())));

        return List.of(
                new ColumnImpl(EMPTY_STRING, table.getTable().getDefaultEditor(Boolean.class), table.getTable().getDefaultRenderer(Boolean.class), 30),
                new ColumnImpl("Key", new DefaultTextCellEditable(keyAutoComplete), new DefaultTextCellRenderer()),
                new ColumnImpl("Value", new DefaultTextCellEditable(valueAutoComplete), new DefaultTextCellRenderer()),
                new ColumnImpl(EMPTY_STRING, new TableCellAction.TableDeleteButtonCellEditor(e -> {
                    table.deleteSelectRow();
                }), new TableCellAction.TableDeleteButtonRenderer(), 80));
    }

    @Override
    public Object[] createNewRowWithData(KeyValue keyValue) {
        return new Object[]{Boolean.TRUE, keyValue.getKey(), keyValue.getValue(), EMPTY_STRING};
    }

    @Override
    public Object[] createNewEmptyRow() {
        return new Object[]{Boolean.TRUE, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING};
    }

    @Override
    public ToolbarDecoratorFactory createToolbarDecoratorFactory(TableOperator tablePanel) {
        return new DefaultToolbarDecoratorFactory(tablePanel, this);
    }
}
