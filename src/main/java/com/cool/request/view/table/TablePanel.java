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

import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TablePanel extends JPanel {
    private final DefaultTableModel defaultTableModel;
    private final JBTable table = new JBTable();

    public TablePanel(TableModeFactory tableModeFactory) {
        super(new BorderLayout());
        List<TableModeFactory.Column> columns = tableModeFactory.createColumn(table);

        Object[] columnNames = columns.stream().map(TableModeFactory.Column::getName).toArray();
        defaultTableModel = new DefaultTableModel(null, columnNames);
        table.setModel(defaultTableModel);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(columns.get(i).getTableCellRenderer());
            if (columns.get(i).getMaxWidth()!=-1){
                table.getColumnModel().getColumn(i).setMaxWidth(columns.get(i).getMaxWidth());
            }
        }
        ToolbarDecoratorFactory toolbarDecoratorFactory = tableModeFactory.createToolbarDecoratorFactory(this);
        add(ToolbarDecorator.createDecorator(table)
                .addExtraActions(toolbarDecoratorFactory.getExtraActions().toArray(new AnActionButton[0]))
                .setPanelBorder(toolbarDecoratorFactory.getPanelBorder())
                .setToolbarBorder(toolbarDecoratorFactory.getToolbarBorder())
                .setScrollPaneBorder(new CustomLineBorder(0, 0, 0, 0))
                .createPanel(), BorderLayout.CENTER);
    }

    protected void addNewEmptyRow(Object[] rowData) {
        defaultTableModel.addRow(rowData);
        defaultTableModel.fireTableDataChanged();
    }
}
