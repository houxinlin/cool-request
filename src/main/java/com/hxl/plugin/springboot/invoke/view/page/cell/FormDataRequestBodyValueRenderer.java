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
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());
         boolean exist =false;
         try {
             exist =Files.exists(Paths.get(value.toString()));
         }catch (Exception ignored){}

        if (value!=null  && !"".equals(value.toString()) && exist){
            jPanel.add(new JLabel("1 file selected"));
        }else{
            jPanel.add(new JLabel("0 file selected"));
        }
        return jPanel;
    }
}