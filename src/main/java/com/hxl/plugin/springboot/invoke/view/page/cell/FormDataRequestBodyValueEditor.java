package com.hxl.plugin.springboot.invoke.view.page.cell;

import com.hxl.plugin.springboot.invoke.utils.file.FileChooseUtils;
import com.intellij.icons.AllIcons;
import org.jetbrains.plugins.notebooks.visualization.r.inlays.components.SaveOutputAction;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

public class FormDataRequestBodyValueEditor extends DefaultCellEditor {
    private final JTextField textJTextField = new JTextField();
    private final JTextField fileJTextField = new JTextField();
    private final CardLayout cardLayout = new CardLayout();
    private final JTable table;
    private final JPanel root = new JPanel(cardLayout);
    private String name;

    public FormDataRequestBodyValueEditor(JTable jTable) {
        super(new JTextField());
        this.table = jTable;

        JLabel fileSelectJLabel = new JLabel(AllIcons.General.OpenDisk);
        fileSelectJLabel.setSize(50,50);
        JPanel fileSelectJPanel = new JPanel(new BorderLayout());
        fileSelectJPanel.add(fileJTextField, BorderLayout.CENTER);
        fileSelectJPanel.add(fileSelectJLabel, BorderLayout.EAST);
        fileSelectJLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                String file = FileChooseUtils.getFile();
                if (file == null) return;
                int editingRow = jTable.getEditingRow();
                jTable.setValueAt(file, editingRow, 1);
                fileJTextField.setText(file);
            }
        });
        JPanel textSelectJPanel = new JPanel(new BorderLayout());
        textSelectJPanel.add(textJTextField, BorderLayout.CENTER);
        root.add("file", fileSelectJPanel);
        root.add("text", textSelectJPanel);
    }


    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        boolean isText = table.getValueAt(row, 2).equals("text");
        cardLayout.show(root, isText ? "text" : "file");
        name = isText ? "text" : "file";
        textJTextField.setOpaque(true);
        fileJTextField.setOpaque(true);
        textJTextField.setText(table.getValueAt(row, column).toString());
        fileJTextField.setText(table.getValueAt(row, column).toString());

        textJTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        fileJTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return root;
    }

    public void setCellEditorValue(String value){
        fileJTextField.setText(value);
    }
    @Override
    public Object getCellEditorValue() {
        if (name.equalsIgnoreCase("file")) return fileJTextField.getText();
        return textJTextField.getText();
    }
}
