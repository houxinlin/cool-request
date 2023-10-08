package com.hxl.plugin.springboot.invoke.view.page.cell;

import com.intellij.icons.AllIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FormDataRequestBodyValueRenderer extends JPanel implements TableCellRenderer {
    private final JPanel fileSelectJPanel = new JPanel(new BorderLayout());
    private final JPanel textSelectJPanel = new JPanel(new BorderLayout());
    private final JTextField textJTextField = new JTextField();
    private final JTextField fileJTextField = new JTextField();
    private final JLabel fileSelectJLabel =new JLabel(AllIcons.Actions.AddFile);

//    public Component getTableCellRendererComponent(JTable table, Object value,
//                                                   boolean isSelected, boolean hasFocus, int row, int column) {
//        if (table.getValueAt(row, 2).equals("text")) return textSelectJPanel;
//
//        JPanel jPanel = new JPanel(new BorderLayout());
//        jPanel.add(new JTextField(), BorderLayout.CENTER);
//        jPanel.add(new JButton("select "), BorderLayout.EAST);
//
//        return jPanel;
//    }

    public FormDataRequestBodyValueRenderer() {
        fileSelectJPanel.add(fileJTextField,BorderLayout.CENTER);
        fileSelectJPanel.add(fileSelectJLabel,BorderLayout.EAST);

        textSelectJPanel.add(textJTextField,BorderLayout.CENTER);

    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {
        textJTextField.setText(value.toString());
        if (table.getValueAt(row, 2).equals("text")) return textSelectJPanel;
        fileJTextField.setText(value.toString());
        return fileSelectJPanel;
    }
}