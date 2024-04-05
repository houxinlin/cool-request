/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BaseTablePanelWithToolbarPanelImpl.java is part of Cool Request
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

package com.cool.request.view.page;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.view.BaseTablePanelParamWithToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本table面板实现，具有增加、删除、复制行的实现
 */
public abstract class BaseTablePanelWithToolbarPanelImpl extends BaseTablePanelParamWithToolbar {
    protected abstract Object[] getTableHeader();

    protected abstract Object[] getNewNullRowData();

    protected List<Integer> getSelectRow() {
        return new ArrayList<>();
    }

    protected abstract void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel);

    private final DefaultTableModel defaultTableModel = new DefaultTableModel(null, getTableHeader());
    private final Project project;
    protected Window window;

    public BaseTablePanelWithToolbarPanelImpl(Project project, Window window) {
        super(new ToolbarBuilder().enabledAdd().enabledRemove().enabledCopyRow());
        this.project = project;
        this.window = window;
        init();
    }

    public BaseTablePanelWithToolbarPanelImpl(Project project) {
        super(new ToolbarBuilder().enabledAdd().enabledRemove().enabledCopyRow());
        this.project = project;
        init();
    }

    public BaseTablePanelWithToolbarPanelImpl(Project project, ToolbarBuilder builder) {
        super(builder);
        this.project = project;
        init();
    }

    public Project getProject() {
        return project;
    }

    @Override
    public void addRow() {
        addNewRow(getNewNullRowData());

    }

    @Override
    public void copyRow() {
        stopEditor();
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow != -1) {
            stopEditor();
            int columnCount = defaultTableModel.getColumnCount();
            Object[] data = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                data[i] = defaultTableModel.getValueAt(selectedRow, i);
            }
            defaultTableModel.addRow(data);
        }
        defaultTableModel.fireTableDataChanged();

    }

    @Override
    public void removeRow() {
        stopEditor();
        List<Integer> selectRow = getSelectRow();
        for (int i = selectRow.size() - 1; i >= 0; i--) {
            defaultTableModel.removeRow(selectRow.get(i));
        }
        jTable.invalidate();
    }

    public void removeClickRow() {
        stopEditor();
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow == -1) return;
        defaultTableModel.removeRow(selectedRow);
        jTable.invalidate();
    }

    public void stopEditor() {
        if (jTable.isEditing()) {
            TableCellEditor cellEditor = jTable.getCellEditor();
            cellEditor.stopCellEditing();
            cellEditor.cancelCellEditing();
        }
    }

    public void removeAllRow() {
        stopEditor();
        while (defaultTableModel.getRowCount() > 0) {
            defaultTableModel.removeRow(0);
        }
        defaultTableModel.fireTableDataChanged();
    }

    protected void addNewRow(Object[] objects) {
        defaultTableModel.addRow(objects);
        jTable.revalidate();
        jTable.invalidate();
        defaultTableModel.fireTableDataChanged();
    }

    protected void foreachTable(java.util.function.BiConsumer<List<Object>, Integer> consumer) {
        for (int row = 0; row < defaultTableModel.getRowCount(); row++) {
            List<Object> itemRow = new ArrayList<>();
            for (int col = 0; col < defaultTableModel.getColumnCount(); col++) {
                itemRow.add(defaultTableModel.getValueAt(row, col));
            }
            consumer.accept(itemRow, row);
        }
    }

    private void init() {
        setLayout(new BorderLayout());
        jTable.setModel(defaultTableModel);

        jTable.setSelectionBackground(CoolRequestConfigConstant.Colors.TABLE_SELECT_BACKGROUND);
        initDefaultTableModel(jTable, defaultTableModel);
        jTable.setBorder(null);
        setBorder(null);
        showToolBar();
        updateUI();
    }

    public DefaultTableModel getDefaultTableModel() {
        return defaultTableModel;
    }

    public JBTable getTable() {
        return jTable;
    }
}
