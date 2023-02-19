package com.hxl.plugin.springbootschedulerinvoke;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ClientsTableButtonRenderer  extends JButton implements TableCellRenderer {
    public ClientsTableButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}