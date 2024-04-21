/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TablePanel.java is part of Cool Request
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

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TablePanel extends JPanel implements TableOperator {
    private final DefaultTableModel defaultTableModel;
    private final JBTable table = new JBTable();
    private final Map<Integer, List<TableDataChange>> dataChangeListener = new HashMap<>();
    private final TableModeFactory<?> tableModeFactory;

    public TablePanel(TableModeFactory<?> tableModeFactory) {
        super(new BorderLayout());
        List<TableModeFactory.Column> columns = tableModeFactory.createColumn(this);
        this.tableModeFactory = tableModeFactory;

        Object[] columnNames = columns.stream().map(TableModeFactory.Column::getName).toArray();
        defaultTableModel = new DefaultTableModel(null, columnNames);
        table.setModel(defaultTableModel);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(columns.get(i).getTableCellRenderer());
            table.getColumnModel().getColumn(i).setCellEditor(columns.get(i).getTableCellEditor());
            if (columns.get(i).getMaxWidth() != -1) {
                table.getColumnModel().getColumn(i).setMaxWidth(columns.get(i).getMaxWidth());
                table.getColumnModel().getColumn(i).setWidth(columns.get(i).getMaxWidth());
                table.getColumnModel().getColumn(i).setPreferredWidth(columns.get(i).getMaxWidth());
                table.getColumnModel().getColumn(i).setMinWidth(columns.get(i).getMaxWidth());
            }
        }
        table.setSelectionBackground(CoolRequestConfigConstant.Colors.TABLE_SELECT_BACKGROUND);
        ToolbarDecoratorFactory toolbarDecoratorFactory = tableModeFactory.createToolbarDecoratorFactory(this);
        add(ToolbarDecorator.createDecorator(table)
                .addExtraActions(toolbarDecoratorFactory.getExtraActions().toArray(new AnActionButton[0]))
                .setPanelBorder(toolbarDecoratorFactory.getPanelBorder())
                .setToolbarBorder(toolbarDecoratorFactory.getToolbarBorder())
                .setScrollPaneBorder(new CustomLineBorder(0, 0, 0, 0))
                .createPanel(), BorderLayout.CENTER);


        installListener();
    }

    public TableModeFactory<?> getTableModeFactory() {
        return tableModeFactory;
    }

    private void installListener() {
        defaultTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                dataChangeListener.forEach((listenerCol, tableDataChanges) -> {
                    if (column == listenerCol) {
                        for (TableDataChange tableDataChange : tableDataChanges) {
                            tableDataChange.onDataChange(row, column);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteSelectRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        this.stopEditor();
        defaultTableModel.removeRow(selectedRow);
        defaultTableModel.fireTableDataChanged();
    }

    @Override
    public JTable getTable() {
        return table;
    }

    @Override
    public void registerTableDataChangeEvent(int listenerCol, TableDataChange tableDataChange) {
        dataChangeListener.computeIfAbsent(listenerCol, integer -> new ArrayList<>()).add(tableDataChange);

    }

    @Override
    public Object getValueAt(int row, int col) {
        return defaultTableModel.getValueAt(row, col);
    }

    @Override
    public void stopEditor() {
        if (table.isEditing()) {
            TableCellEditor cellEditor = table.getCellEditor();
            cellEditor.stopCellEditing();
            cellEditor.cancelCellEditing();
        }
    }

    @Override
    public List<TableDataRow> listTableData(Function<TableDataRow, Boolean> filter) {
        List<TableDataRow> result = new ArrayList<>();
        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            Object[] data = new Object[defaultTableModel.getColumnCount()];
            for (int i1 = 0; i1 < defaultTableModel.getColumnCount(); i1++) {
                data[i1] = defaultTableModel.getValueAt(i, i1);
            }
            TableDataRow tableDataRow = new TableDataRow(i, defaultTableModel.getColumnCount(), data);
            if (filter.apply(tableDataRow)) result.add(tableDataRow);
        }
        return result;
    }

    @Override
    public void removeAllData() {
        defaultTableModel.setRowCount(0);
        defaultTableModel.fireTableDataChanged();
    }

    @Override
    public void addNewRow(Object[] rowData) {
        defaultTableModel.addRow(rowData);
        defaultTableModel.fireTableDataChanged();
    }

    @Override
    public void removeRow(int rowIndex) {
        defaultTableModel.removeRow(rowIndex);
        defaultTableModel.fireTableDataChanged();
    }

    @Override
    public Object[] getSelectData() {
        stopEditor();
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Object[] data = new Object[defaultTableModel.getColumnCount()];
            for (int i1 = 0; i1 < defaultTableModel.getColumnCount(); i1++) {
                data[i1] = defaultTableModel.getValueAt(selectedRow, i1);
            }
            return data;
        }
        return null;
    }
}
