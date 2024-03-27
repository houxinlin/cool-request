/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ButtonColumn.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class ButtonColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener {
    private static final Color SELECTED_COLOR=Color.getColor("#03a9f4");
    private final JTable table;
    private final Action action;
    private final Border originalBorder;
    private final JButton renderButton;
    private final JButton editButton;
    private Object editorValue;
    private boolean isButtonColumnEditor;

    public ButtonColumn(JTable table, Action action, int column) {
        this.table = table;
        this.action = action;

        renderButton = new JButton();
        editButton = new JButton();
        editButton.setFocusPainted(false);
        editButton.addActionListener(this);
        originalBorder = editButton.getBorder();

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    public void setMnemonic(int mnemonic) {
        renderButton.setMnemonic(mnemonic);
        editButton.setMnemonic(mnemonic);
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        if (value == null) {
            editButton.setText("");
            editButton.setIcon(null);
        } else if (value instanceof Icon) {
            editButton.setText("");
            editButton.setIcon((Icon) value);
        } else {
            editButton.setText(value.toString());
            editButton.setIcon(null);
        }
        if (isSelected) {
            editButton.setBorder(  new ZoneBorder(SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR));
        } else {
        }
        this.editorValue = value;
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout ());
        renderButton.setSize(20,20);
        jPanel.add(editButton,BorderLayout.CENTER);
        jPanel.setBorder(   new ZoneBorder(SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR));
        return jPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return editorValue;
    }

//
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            renderButton.setForeground(table.getSelectionForeground());
            renderButton.setBackground(table.getSelectionBackground());
            renderButton.setBorder(  new ZoneBorder(SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR));
        } else {
            renderButton.setForeground(table.getForeground());
            renderButton.setBackground(UIManager.getColor("Button.background"));
        }

        if (hasFocus) {
            renderButton.setBorder(  new ZoneBorder(SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR));
        } else {
            renderButton.setBorder(originalBorder);
        }
        if (value == null) {
            renderButton.setText("");
            renderButton.setIcon(null);
        } else if (value instanceof Icon) {
            renderButton.setText("");
            renderButton.setIcon((Icon) value);
        } else {
            renderButton.setText(value.toString());
            renderButton.setIcon(null);
        }
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout ());
        renderButton.setSize(20,20);
        jPanel.add(renderButton,BorderLayout.CENTER);
//        jPanel.setBorder(   new ZoneBorder(SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR,SELECTED_COLOR));
        return jPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int row = table.convertRowIndexToModel(table.getEditingRow());
        fireEditingStopped();

        //  Invoke the Action

        ActionEvent event = new ActionEvent(
                table,
                ActionEvent.ACTION_PERFORMED,
                "" + row);
        action.actionPerformed(event);
    }

    public void mousePressed(MouseEvent e) {
        if (table.isEditing()
                && table.getCellEditor() == this)
            isButtonColumnEditor = true;
    }

    public void mouseReleased(MouseEvent e) {
        if (isButtonColumnEditor
                && table.isEditing())
            table.getCellEditor().stopCellEditing();

        isButtonColumnEditor = false;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}