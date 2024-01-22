package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.ClassResourceUtils;
import com.hxl.plugin.springboot.invoke.view.page.BaseJTablePanelWithToolbar;
import com.hxl.plugin.springboot.invoke.view.page.cell.DefaultJTextCellEditable;
import com.hxl.plugin.springboot.invoke.view.page.cell.DefaultJTextCellRenderer;
import com.hxl.plugin.springboot.invoke.view.table.TableCellAction;
import com.hxl.plugin.springboot.invoke.view.widget.AutoCompleteJTextField;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class BasicKeyValueTableParamJPanel extends BaseJTablePanelWithToolbar {
    private AutoCompleteJTextField keyAutoComplete;

    private AutoCompleteJTextField valueAutoComplete;

    public BasicKeyValueTableParamJPanel(Project project) {
        super(project);
    }

    @Override
    protected Object[] getTableHeader() {
        return new Object[]{"", "Key", "Value", ""};
    }

    @Override
    protected Object[] getNewRowData() {
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

        keyAutoComplete = new AutoCompleteJTextField(getKeySuggest(), getProject());
        valueAutoComplete = new AutoCompleteJTextField(getKeySuggest(), getProject());

        jTable.getColumnModel().getColumn(1).setCellEditor(new DefaultJTextCellEditable(keyAutoComplete, getProject()));
        jTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultJTextCellRenderer());

        jTable.getColumnModel().getColumn(2).setCellEditor(new DefaultJTextCellEditable(valueAutoComplete, getProject()));
        jTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultJTextCellRenderer());

        jTable.getColumnModel().getColumn(0).setMaxWidth(30);
        jTable.getColumnModel().getColumn(3).setMaxWidth(80);

        jTable.getColumnModel().getColumn(3).setCellEditor(new TableCellAction.TableDeleteButtonCellEditor(this::deleteActionPerformed));
        jTable.getColumnModel().getColumn(3).setCellRenderer(new TableCellAction.TableDeleteButtonRenderer());

        defaultTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 1) {
                    valueAutoComplete.setSuggest(getValueSuggest(defaultTableModel.getValueAt(row, 1).toString()));
                }
            }
        });
    }


    private static Object[] getNewRow(Object key, Object value) {
        return new Object[]{true, key, value, ""};
    }

    public void setTableData(List<KeyValue> headers) {
        if (headers == null) headers = new ArrayList<>();
        headers.add(new KeyValue("", ""));
        removeAllRow();
        for (KeyValue header : headers) {
            addNewRow(new Object[]{true, header.getKey(), header.getValue(), ""});
        }
    }

    public List<KeyValue> getTableMap() {
        List<KeyValue> result = new ArrayList<>();
        foreach((s, s2) -> {
            result.add(new KeyValue(s, s2));
        });
        return result;
    }

    public void foreach(BiConsumer<String, String> biConsumer) {
        foreachTable(objects -> {
            if (Boolean.valueOf(objects.get(0).toString())) {
                String key = objects.get(1).toString();
                String value = objects.get(2).toString();
                if (!("".equals(key))) {
                    biConsumer.accept(key, value);
                }
            }

        });

    }

//    public void deleteActionPerformed(ActionEvent e) {
//        removeRow();
//        int rowCount = defaultTableModel.getRowCount();
//        int emptyValues = 0;
//        for (int i = 0; i < rowCount; i++) {
//            if (defaultTableModel.getValueAt(i, 0).toString().isEmpty() && defaultTableModel.getValueAt(i, 1).toString().isEmpty()) {
//                emptyValues++;
//            }
//        }
//        int modelRow = Integer.parseInt(e.getActionCommand());
//        //如果删除的是空行，并且空行数需要至少一个才能删除
//        if ((defaultTableModel.getValueAt(modelRow, 0).toString().isEmpty() &&
//                defaultTableModel.getValueAt(modelRow, 1).toString().isEmpty() &&
//                emptyValues > 1) || (!defaultTableModel.getValueAt(modelRow, 0).toString().isEmpty() || !
//                defaultTableModel.getValueAt(modelRow, 1).toString().isEmpty())) {
//            ((DefaultTableModel) jTable.getModel()).removeRow(modelRow);
//        }
//        if (jTable.getModel().getRowCount() == 0) {
//            defaultTableModel.addRow(getNewRow());
//        }
//    }


}
