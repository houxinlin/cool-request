package com.cool.request.view.page;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.view.BaseTablePanelParamWithToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
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


    public BaseTablePanelWithToolbarPanelImpl(Project project) {
        super(project, new ToolbarBuilder().enabledAdd().enabledRemove().enabledCopyRow());
        this.project = project;
        init();
    }

    public BaseTablePanelWithToolbarPanelImpl(Project project, ToolbarBuilder builder) {
        super(project, builder);
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
        defaultTableModel.fireTableDataChanged();
        jTable.clearSelection();
        jTable.invalidate();
        jTable.updateUI();
    }

    public void removeClickRow() {
        stopEditor();
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow == -1) return;
        defaultTableModel.removeRow(selectedRow);
        defaultTableModel.fireTableDataChanged();
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
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(jTable);
        scrollPane.setOpaque(false);
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
