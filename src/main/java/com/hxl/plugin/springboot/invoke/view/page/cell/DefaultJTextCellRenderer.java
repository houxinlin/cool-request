package com.hxl.plugin.springboot.invoke.view.page.cell;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DefaultJTextCellRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JTextField jTextField = new JTextField(value.toString());
        jTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        jTextField.setBorder(null);
        return jTextField;
    }
}
