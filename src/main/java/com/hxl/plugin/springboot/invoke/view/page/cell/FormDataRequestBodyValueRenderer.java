package com.hxl.plugin.springboot.invoke.view.page.cell;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FormDataRequestBodyValueRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if ( table.getValueAt(row,2).equals("text")) return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);

        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(new JTextField(),BorderLayout.CENTER);
        jPanel.add(new JButton("select "),BorderLayout.EAST);

        return jPanel;
    }
}