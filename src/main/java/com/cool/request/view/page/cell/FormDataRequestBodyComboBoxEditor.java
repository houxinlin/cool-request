package com.cool.request.view.page.cell;

import com.cool.request.common.constant.CoolRequestConfigConstant;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ItemEvent;

public class FormDataRequestBodyComboBoxEditor extends DefaultCellEditor implements TableCellEditor {
    private JComboBox<String> comboBox;
    public FormDataRequestBodyComboBoxEditor(JTable jTable) {
        super(new JComboBox<>(new String[]{CoolRequestConfigConstant.Identifier.FILE, CoolRequestConfigConstant.Identifier.TEXT}));
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
