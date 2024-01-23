package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.lib.curl.ArgumentHolder;
import com.hxl.plugin.springboot.invoke.lib.curl.BasicCurlParser;
import com.hxl.plugin.springboot.invoke.lib.curl.StringArgumentHolder;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.net.MediaTypes;
import com.hxl.plugin.springboot.invoke.tool.ProviderManager;
import com.hxl.plugin.springboot.invoke.utils.MediaTypeUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.utils.UrlUtils;
import com.hxl.plugin.springboot.invoke.view.BaseTablePanelParamWithToolbar;
import com.hxl.plugin.springboot.invoke.view.IRequestParamManager;
import com.hxl.plugin.springboot.invoke.view.dialog.BigInputDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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


    public BaseTablePanelWithToolbarPanelImpl(Project project) {
        super(project, true);
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
