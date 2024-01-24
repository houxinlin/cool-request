package com.cool.request.view.page;

import com.cool.request.Constant;
import com.cool.request.view.BaseTablePanelParamWithToolbar;
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

/**
 * 基本table面板实现，具有增加、删除、复制行的实现
 */
public abstract class BaseTablePanelWithToolbarPanelImpl extends BaseTablePanelParamWithToolbar {
    protected abstract Object[] getTableHeader();

    protected abstract Object[] getNewRowData();

    protected abstract void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel);

    private final DefaultTableModel defaultTableModel = new DefaultTableModel(null, getTableHeader());
    private JBTable jTable;
    private final Project project;

    private Window window;

    public BaseTablePanelWithToolbarPanelImpl(Project project) {
        super(project, true);
        this.project = project;
        init();
        showToolBar();
    }

    public BaseTablePanelWithToolbarPanelImpl(Project project, Window window) {
        super(project, true);
        this.project = project;
        this.window = window;
        init();
        showToolBar();
    }

    public Window getWindow() {
        return window;
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
            @Override
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
