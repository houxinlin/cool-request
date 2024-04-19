/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FormDataRequestBodyPage.java is part of Cool Request
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

import com.cool.request.components.http.FormDataInfo;
import com.cool.request.view.page.cell.*;
import com.cool.request.view.table.TableCellAction;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;


public class FormDataRequestBodyPage extends BaseTablePanelWithToolbarPanelImpl {

    @Override
    protected Object[] getTableHeader() {
        return new Object[]{"", "Key", "Value", "Type", ""};
    }

    @Override
    protected Object[] getNewNullRowData() {
        return new Object[]{true, "", "", "text", ""};
    }

    public FormDataRequestBodyPage(Project project) {
        super(project);

    }

    @Override
    protected List<Integer> getSelectRow() {
        List<Integer> result = new ArrayList<>();
        foreachTable((objects, row) -> {
            if (Boolean.parseBoolean(objects.get(0).toString())) {
                result.add(row);
            }
        });
        return result;
    }

    @Override
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        jTable.getColumnModel().getColumn(0).setMaxWidth(30);
        jTable.getColumnModel().getColumn(4).setMaxWidth(80);

        jTable.getColumnModel().getColumn(0).setCellRenderer(jTable.getDefaultRenderer(Boolean.class));
        jTable.getColumnModel().getColumn(0).setCellEditor(jTable.getDefaultEditor(Boolean.class));

        jTable.getColumnModel().getColumn(1).setCellEditor(new DefaultJTextCellEditable());
        jTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTextCellRenderer());


        jTable.getColumnModel().getColumn(2).setCellRenderer(new FormDataRequestBodyValueRenderer());
        jTable.getColumnModel().getColumn(2).setCellEditor(new FormDataRequestBodyValueCellEditor(jTable, getProject()));

        jTable.getColumnModel().getColumn(3).setCellRenderer(new FormDataRequestBodyComboBoxRenderer(jTable));
        jTable.getColumnModel().getColumn(3).setCellEditor(new FormDataRequestBodyComboBoxEditor(jTable));

        jTable.getColumnModel().getColumn(4).setCellEditor(new TableCellAction.TableDeleteButtonCellEditor((e) -> removeClickRow()));
        jTable.getColumnModel().getColumn(4).setCellRenderer(new TableCellAction.TableDeleteButtonRenderer());

    }


    public void setFormData(List<FormDataInfo> value, boolean addNewLine) {
        if (value == null) value = new ArrayList<>();
        if (addNewLine) {
            value = new ArrayList<>(value);
            value.addAll(value);
            value.add(new FormDataInfo("", "", "text"));

        }
        removeAllRow();
        for (FormDataInfo formDataInfo : value) {
            addNewRow(new Object[]{true, formDataInfo.getName(), formDataInfo.getValue(), formDataInfo.getType(), ""});
        }
    }

    public void setFormData(List<FormDataInfo> value) {
        setFormData(value, false);
    }

    public List<FormDataInfo> getFormData() {
        List<FormDataInfo> result = new ArrayList<>();
        foreachTable((objects, integer) -> {
            if (!Boolean.parseBoolean(objects.get(0).toString())) return;
            String key = objects.get(1).toString();
            if ("".equalsIgnoreCase(key)) return;
            result.add(new FormDataInfo(key,
                    objects.get(2).toString(),
                    objects.get(3).toString()));
        });
        return result;
    }


}