package com.hxl.plugin.springboot.invoke.view.page.cell;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ItemEvent;

public class FormDataRequestBodyComboBoxEditor extends DefaultCellEditor implements TableCellEditor  {
    private JComboBox<String> comboBox;
    public FormDataRequestBodyComboBoxEditor(JTable jTable) {
        super(new JComboBox<>(new String[]{"file", "text"}));
        comboBox = (JComboBox<String>) getComponent();
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //下拉框改变后清楚当前的值
               jTable.getModel().setValueAt("",jTable.getEditingRow(),1);
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
}
