package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.view.BaseTableParamWithToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseJTablePanelWithToolbar extends BaseTableParamWithToolbar {
    protected abstract Object[] getTableHeader();

    protected abstract Object[] getNewRowData();

    protected abstract void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel);

    private final DefaultTableModel defaultTableModel = new DefaultTableModel(null, getTableHeader());
    private JBTable jTable;
    private final Project project;

    public BaseJTablePanelWithToolbar(Project project) {
        super(true);
        this.project = project;
        init();
        showToolBar();
    }

    public Project getProject() {
        return project;
    }

    protected void deleteActionPerformed(ActionEvent e) {
        removeRow();
    }

    @Override
    public void addRow() {
        addNewRow(getNewRowData());
    }

    @Override
    public void copyRow() {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow != -1) {
            int columnCount = defaultTableModel.getColumnCount();
            Object[] data = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                data[i] = defaultTableModel.getValueAt(selectedRow, i);
            }
            defaultTableModel.addRow(data);
        }

    }

    @Override
    public void removeRow() {
        stopEditor();
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow == -1) return;
        defaultTableModel.removeRow(selectedRow);
        jTable.clearSelection();
        jTable.invalidate();
        jTable.updateUI();
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
    }

    protected void addNewRow(Object[] objects) {
        defaultTableModel.addRow(objects);
        jTable.revalidate();
        jTable.invalidate();
    }

    protected void foreachTable(java.util.function.Consumer<List<Object>> consumer) {
        for (int row = 0; row < defaultTableModel.getRowCount(); row++) {
            List<Object> itemRow = new ArrayList<>();
            for (int col = 0; col < defaultTableModel.getColumnCount(); col++) {
                itemRow.add(defaultTableModel.getValueAt(row, col));
            }
            consumer.accept(itemRow);
        }
    }

    private void init() {
        setLayout(new BorderLayout());
        defaultTableModel.addRow(getNewRowData());
        jTable = new JBTable(defaultTableModel) {
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        jTable.setSelectionBackground(Constant.Colors.TABLE_SELECT_BACKGROUND);
        initDefaultTableModel(jTable, defaultTableModel);
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(jTable);
        setContent(scrollPane);
        scrollPane.setOpaque(false);

        jTable.setBorder(null);
        setBorder(null);
    }
}
