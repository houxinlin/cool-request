package com.hxl.plugin.springboot.invoke.view.page.cell;

import com.intellij.icons.AllIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FormDataRequestBodyValueRenderer extends JPanel implements TableCellRenderer {
    private final JTextField textJTextField = new JTextField();
    private final JTextField fileJTextField = new JTextField();
    private final CardLayout cardLayout =new CardLayout();

    public FormDataRequestBodyValueRenderer() {
        this.setLayout(cardLayout);
        setOpaque(true);
        JPanel fileSelectJPanel = new JPanel(new BorderLayout());
        fileSelectJPanel.add(fileJTextField,BorderLayout.CENTER);
        JLabel fileSelectJLabel = new JLabel(AllIcons.General.OpenDisk);
        fileSelectJPanel.add(fileSelectJLabel,BorderLayout.EAST);
        fileSelectJPanel.setOpaque(true);

        JPanel textSelectJPanel = new JPanel(new BorderLayout());
        textSelectJPanel.add(textJTextField,BorderLayout.CENTER);
        textSelectJPanel.setOpaque(true);
        this.add("file", fileSelectJPanel);
        this.add("text", textSelectJPanel);

    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {
        if (table.getValueAt(row, 3).equals("text")){
            cardLayout.show(this,"text");
        }else{
            cardLayout.show(this,"file");
        }
        textJTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        fileJTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        textJTextField.setText(table.getValueAt(row,column).toString());
        fileJTextField.setText(table.getValueAt(row,column).toString());

        this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        return this;
    }
}