package com.cool.request.view.page.cell;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class DefaultJTextCellRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String newValue = value == null ? "" : value.toString();
        JTextField jTextField = new JTextField(newValue);
        jTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        jTextField.setBorder(null);
        return jTextField;
    }
}
