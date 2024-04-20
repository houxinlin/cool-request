/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BasicKeyValueTablePanelParamPanel.java is part of Cool Request
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

package com.cool.request.view;

import com.cool.request.components.http.KeyValue;
import com.cool.request.view.page.BaseTablePanelWithToolbarPanelImpl;
import com.cool.request.view.page.cell.DefaultTextCellEditable;
import com.cool.request.view.page.cell.DefaultTextCellRenderer;
import com.cool.request.view.table.TableCellAction;
import com.cool.request.view.widget.AutocompleteField;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BasicKeyValueTablePanelParamPanel extends BaseTablePanelWithToolbarPanelImpl {
    private AutocompleteField keyAutoComplete;
    private AutocompleteField valueAutoComplete;

    public BasicKeyValueTablePanelParamPanel(Project project, Window window) {
        super(project, window);
    }

    public BasicKeyValueTablePanelParamPanel(Project project) {
        super(project);
    }

    @Override
    protected Object[] getTableHeader() {
        return new Object[]{"", "Key", "Value", ""};
    }

    @Override
    protected Object[] getNewNullRowData() {
        return new Object[]{true, "", "", ""};
    }

    protected List<String> getKeySuggest() {
        return new ArrayList<>();
    }

    protected List<String> getValueSuggest(String key) {
        return new ArrayList<>();
    }

    @Override
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        jTable.getColumnModel().getColumn(0).setCellEditor(jTable.getDefaultEditor(Boolean.class));
        jTable.getColumnModel().getColumn(0).setCellRenderer(jTable.getDefaultRenderer(Boolean.class));
        Function<String, List<String>> lookup = text -> getKeySuggest().stream()
                .filter(v -> !text.isEmpty() && v.toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());

        keyAutoComplete = new AutocompleteField(lookup, window);
        valueAutoComplete = new AutocompleteField(s -> new ArrayList<>());
        jTable.getColumnModel().getColumn(1).setCellEditor(new DefaultTextCellEditable(keyAutoComplete));
        jTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTextCellRenderer());

        jTable.getColumnModel().getColumn(2).setCellEditor(new DefaultTextCellEditable(valueAutoComplete));
        jTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTextCellRenderer());

        jTable.getColumnModel().getColumn(0).setMaxWidth(30);
        jTable.getColumnModel().getColumn(3).setMaxWidth(80);

        jTable.getColumnModel().getColumn(3).setCellEditor(new TableCellAction.TableDeleteButtonCellEditor(e -> removeClickRow()));
        jTable.getColumnModel().getColumn(3).setCellRenderer(new TableCellAction.TableDeleteButtonRenderer());

        defaultTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 1) {
                    valueAutoComplete.setLookup(target -> getValueSuggest(defaultTableModel.getValueAt(row, 1).toString())
                            .stream().filter(s -> s.startsWith(target)).collect(Collectors.toList()));

                }
            }
        });
    }

    @Override
    protected List<Integer> getSelectRow() {
        List<Integer> rows = new ArrayList<>();
        foreachTable((objects, row) -> {
            if (Boolean.valueOf(objects.get(0).toString())) {
                rows.add(row);
            }
        });
        return rows;
    }

    public void setTableData(List<KeyValue> headers, boolean addNewLine) {
        if (headers == null) headers = new ArrayList<>();
        if (addNewLine) {
            headers.add(new KeyValue("", ""));
        }
        removeAllRow();
        for (KeyValue header : headers) {
            addNewRow(new Object[]{true, header.getKey(), header.getValue(), ""});
        }
    }

    public void setTableData(List<KeyValue> headers) {
        setTableData(headers, false);
    }

    public List<KeyValue> getTableMap() {
        List<KeyValue> result = new ArrayList<>();
        foreach((s, s2) -> {
            result.add(new KeyValue(s, s2));
        });
        return result;
    }

    public void foreach(BiConsumer<String, String> biConsumer) {
        foreachTable((objects, integer) -> {
            if (Boolean.valueOf(objects.get(0).toString())) {
                String key = objects.get(1).toString();
                String value = objects.get(2).toString();
                if (!("".equals(key))) {
                    biConsumer.accept(key, value);
                }
            }
        });

    }

}
