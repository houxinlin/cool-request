package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.view.page.cell.*;
import com.hxl.plugin.springboot.invoke.view.table.TableCellAction;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;


public class FormDataRequestBodyPage extends BaseJTablePanelWithToolbar {

    @Override
    protected Object[] getTableHeader() {
        return new Object[]{"", "Key", "Value", "Type", ""};
    }

    @Override
    protected Object[] getNewRowData() {
        return new Object[]{true, "", "", "text", ""};
    }

    @Override
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        jTable.getColumnModel().getColumn(0).setMaxWidth(30);
        jTable.getColumnModel().getColumn(4).setMaxWidth(80);

        jTable.getColumnModel().getColumn(0).setCellRenderer(jTable.getDefaultRenderer(Boolean.class));
        jTable.getColumnModel().getColumn(0).setCellEditor(jTable.getDefaultEditor(Boolean.class));

        jTable.getColumnModel().getColumn(1).setCellEditor(new DefaultJTextCellEditable(getProject()));
        jTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultJTextCellRenderer());



        jTable.getColumnModel().getColumn(2).setCellRenderer(new FormDataRequestBodyValueRenderer());
        jTable.getColumnModel().getColumn(2).setCellEditor(new FormDataRequestBodyValueCellEditor(jTable, getProject()));

        jTable.getColumnModel().getColumn(3).setCellRenderer(new FormDataRequestBodyComboBoxRenderer(jTable));
        jTable.getColumnModel().getColumn(3).setCellEditor(new FormDataRequestBodyComboBoxEditor(jTable));

        jTable.getColumnModel().getColumn(4).setCellEditor(new TableCellAction.TableDeleteButtonCellEditor(this::deleteActionPerformed));
        jTable.getColumnModel().getColumn(4).setCellRenderer(new TableCellAction.TableDeleteButtonRenderer());

    }

    public FormDataRequestBodyPage(Project project) {
        super(project);

    }

    public void setFormData(List<FormDataInfo> value) {
        if (value == null) value = new ArrayList<>();
        value.add(new FormDataInfo("", "", "text"));
        removeAllRow();
        for (FormDataInfo formDataInfo : value) {
            addNewRow(new Object[]{true, formDataInfo.getName(), formDataInfo.getValue(), formDataInfo.getType(), ""});
        }
    }

    public List<FormDataInfo> getFormData() {
        List<FormDataInfo> result = new ArrayList<>();
        foreachTable(objects -> {
            if (!Boolean.valueOf(objects.get(0).toString())) return;
            String key = objects.get(1).toString().toString();
            if ("".equalsIgnoreCase(key)) return;
            result.add(new FormDataInfo(key,
                    objects.get(2).toString().toString(),
                    objects.get(3).toString().toString()));
        });

        return result;
    }


}