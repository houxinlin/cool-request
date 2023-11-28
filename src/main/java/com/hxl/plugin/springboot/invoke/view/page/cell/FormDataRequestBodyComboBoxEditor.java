package com.hxl.plugin.springboot.invoke.view.page.cell;

import com.hxl.plugin.springboot.invoke.Constant;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ItemEvent;

public class FormDataRequestBodyComboBoxEditor extends DefaultCellEditor implements TableCellEditor {
    private JComboBox<String> comboBox;
    public FormDataRequestBodyComboBoxEditor(JTable jTable) {
        super(new JComboBox<>(new String[]{Constant.Identifier.FILE, Constant.Identifier.TEXT}));
        comboBox = (JComboBox<String>) getComponent();
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                jTable.revalidate();
                jTable.invalidate();
            }
        });
        comboBox.setOpaque(true);
    }
    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
}
