package com.hxl.plugin.springboot.invoke.view;

import javax.swing.*;
import java.awt.*;

public class CustomTableCellEditor extends DefaultCellEditor {
    public CustomTableCellEditor() {
        super(new JTextField());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        Component editorComponent = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        // 设置编辑时的边框样式
//        editorComponent.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        return editorComponent;
    }
}
