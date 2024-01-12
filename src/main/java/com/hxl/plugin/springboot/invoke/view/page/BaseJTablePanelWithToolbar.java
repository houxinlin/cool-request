package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.view.BaseTableParamWithToolbar;
import com.hxl.plugin.springboot.invoke.view.page.cell.FormDataRequestBodyComboBoxEditor;
import com.hxl.plugin.springboot.invoke.view.page.cell.FormDataRequestBodyComboBoxRenderer;
import com.hxl.plugin.springboot.invoke.view.page.cell.FormDataRequestBodyValueEditor;
import com.hxl.plugin.springboot.invoke.view.page.cell.FormDataRequestBodyValueRenderer;
import com.hxl.plugin.springboot.invoke.view.table.TableCellAction;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import org.jetbrains.deft.Obj;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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

    public BaseJTablePanelWithToolbar() {
        this(null);
    }

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
        defaultTableModel.removeRow(jTable.getSelectedRow());
    }

    private void stopEditor() {
        if (jTable.isEditing()) {
            jTable.getCellEditor().stopCellEditing();
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
        add(new JScrollPane(jTable), BorderLayout.CENTER);
//        Action delete = new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                JTable table = (JTable) e.getSource();
//                DefaultTableModel model = (DefaultTableModel) table.getModel();
//                int rowCount = model.getRowCount();
//                int emptyValues = 0;
//                for (int i = 0; i < rowCount; i++) {
//                    if (model.getValueAt(i, 0).toString().isEmpty() && model.getValueAt(i, 1).toString().isEmpty()) {
//                        emptyValues++;
//                    }
//                }
//                int modelRow = Integer.parseInt(e.getActionCommand());
//                //如果删除的是空行，并且空行数需要至少一个才能删除
//                if ((model.getValueAt(modelRow, 0).toString().isEmpty() &&
//                        model.getValueAt(modelRow, 1).toString().isEmpty() &&
//                        emptyValues > 1) || (!model.getValueAt(modelRow, 0).toString().isEmpty() || !
//                        model.getValueAt(modelRow, 1).toString().isEmpty())) {
//                    ((DefaultTableModel) table.getModel()).removeRow(modelRow);
//                }
//                if (table.getModel().getRowCount() == 0) {
//                    defaultTableModel.addRow(new String[]{"", "", "text", "Delete"});
//                }
//            }
//        };
//        defaultTableModel.addTableModelListener(e -> {
//            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0
//                    && !defaultTableModel.getValueAt(defaultTableModel.getRowCount() - 1, 0).toString().isEmpty()) {
//                String[] strings = {"", "", "text", "Delete"};
//                defaultTableModel.addRow(strings);
//            }
//        });
    }
}
