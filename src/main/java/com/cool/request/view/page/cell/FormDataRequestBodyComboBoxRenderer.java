package com.cool.request.view.page.cell;

import com.cool.request.Constant;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class FormDataRequestBodyComboBoxRenderer extends DefaultTableCellRenderer {
    private final JComboBox<String> comboBox;
    public FormDataRequestBodyComboBoxRenderer(JTable jTable) {

        comboBox = new ComboBox<>(new String[]{Constant.Identifier.FILE, Constant.Identifier.TEXT});
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        comboBox.setSelectedItem(value);
        comboBox.setBackground(isSelected?table.getSelectionBackground():table.getBackground());
        return comboBox;
    }
}