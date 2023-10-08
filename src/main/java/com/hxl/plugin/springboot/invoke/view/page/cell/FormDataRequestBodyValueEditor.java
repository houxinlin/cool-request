package com.hxl.plugin.springboot.invoke.view.page.cell;

import com.intellij.icons.AllIcons;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class FormDataRequestBodyValueEditor  extends JPanel implements TableCellEditor {
    private final JPanel fileSelectJPanel = new JPanel(new BorderLayout());
    private final JPanel textSelectJPanel = new JPanel(new BorderLayout());
    private final JTextField textJTextField = new JTextField();
    private final JTextField fileJTextField = new JTextField();
    private final JLabel fileSelectJLabel =new JLabel(AllIcons.Actions.AddFile);
    private final JTable table;

    public FormDataRequestBodyValueEditor(JTable table) {
        this.table =table;
        this.setLayout(new BorderLayout());
        fileSelectJLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("a");
            }
        });
        fileSelectJPanel.add(fileJTextField,BorderLayout.CENTER);
        fileSelectJPanel.add(fileSelectJLabel,BorderLayout.EAST);

        textSelectJPanel.add(textJTextField,BorderLayout.CENTER);
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        textJTextField.setText(value.toString());
        if (table.getValueAt(row, 2).equals("text")) return textSelectJPanel;
        fileJTextField.setText(value.toString());
        return fileSelectJPanel;
    }

    public void addCellEditorListener(CellEditorListener l) {
    }

    public void cancelCellEditing() {
    }

    public Object getCellEditorValue() {
        return textJTextField.getText();
    }

    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public void removeCellEditorListener(CellEditorListener l) {

    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
        int row = table.getEditingRow();
        int editingColumn = table.getEditingColumn();
        String value =(table.getValueAt(row, 2).equals("text"))?textJTextField.getText():fileJTextField.getText();
        System.out.println(value);
        table.getModel().setValueAt(value,row,editingColumn);
        return true;
    }
}
