package com.hxl.plugin.springboot.invoke.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    private static final Color SELECTED_COLOR=Color.getColor("#03a9f4");
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);


        if (hasFocus) {
            // 设置编辑时的边框样式
            setBorder(new ZoneBorder(SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR));
        } else {
            // 恢复默认的边框样式
        }
        return cellComponent;
    }
}
