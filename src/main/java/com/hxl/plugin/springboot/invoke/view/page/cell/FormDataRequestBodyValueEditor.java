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
    private final JPanel fileSelectJPanel = new JPanel(new BorderLayout());
    private final JPanel textSelectJPanel = new JPanel(new BorderLayout());
    private final JTextField textJTextField = new JTextField();
    private final JTextField fileJTextField = new JTextField();
    private CardLayout cardLayout = new CardLayout();
    private final JLabel fileSelectJLabel = new JLabel(AllIcons.General.OpenDisk);
    private final JTable table;
    private JPanel root = new JPanel(cardLayout);
    private String name;

    public FormDataRequestBodyValueEditor(JTable jTable) {
        super(new JTextField());
        this.table = jTable;

        fileSelectJLabel.setSize(50,50);
        fileSelectJPanel.add(fileJTextField, BorderLayout.CENTER);
        fileSelectJPanel.add(fileSelectJLabel, BorderLayout.EAST);
        fileSelectJLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String file = FileChooseUtils.getFile();
                System.out.println(file);
                if (file == null) return;
                int editingRow = jTable.getEditingRow();
                jTable.setValueAt(file, editingRow, 1);
                fileJTextField.setText(file);
            }
        });
        textSelectJPanel.add(textJTextField, BorderLayout.CENTER);
        root.add("file", fileSelectJPanel);
        root.add("text", textSelectJPanel);
    }


    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        System.out.println(delegate.getCellEditorValue());

        boolean isText = table.getValueAt(row, 2).equals("text");
        cardLayout.show(root, isText ? "text" : "file");
        name = isText ? "text" : "file";
        textJTextField.setText(table.getValueAt(row, column).toString());
        fileJTextField.setText(table.getValueAt(row, column).toString());

        return root;
    }

    @Override
    public Object getCellEditorValue() {
        if (name.equalsIgnoreCase("file")) return fileJTextField.getText();
        return textJTextField.getText();
    }
    //    public void addCellEditorListener(CellEditorListener l) {
//    }
//
//    public void cancelCellEditing() {
//    }
//
//    public Object getCellEditorValue() {
//        return textJTextField.getText();
//    }
//
//    public boolean isCellEditable(EventObject anEvent) {
//        return true;
//    }
//
//    public void removeCellEditorListener(CellEditorListener l) {
//
//    }
//
//    public boolean shouldSelectCell(EventObject anEvent) {
//        return true;
//    }
//
//    public boolean stopCellEditing() {
//        int row = table.getEditingRow();
//        int editingColumn = table.getEditingColumn();
//        String value = (table.getValueAt(row, 2).equals("text")) ? textJTextField.getText() : fileJTextField.getText();
//        table.getModel().setValueAt(value, row, editingColumn);
//        return true;
//    }
}
