package com.hxl.plugin.springbootschedulerinvoke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientsTableRenderer extends DefaultCellEditor {
    interface Callback {
        public void click(int row);
    }

    private JButton button;
    private String label;
    private boolean clicked;
    private int row, col;
    private JTable table;
    private Callback callback;

    public ClientsTableRenderer(JCheckBox checkBox, Callback callback) {
        super(checkBox);
        this.callback = callback;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        this.col = column;
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        clicked = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (clicked) {
            if (callback != null) callback.click(row);
        }
        clicked = false;
        return new String(label);
    }

    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
