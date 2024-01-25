package com.cool.request.view.page.cell;

import com.cool.request.utils.file.FileChooseUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

public class TextFieldWithBrowseButtonEditable implements TableCellEditor {
    private TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();

    public TextFieldWithBrowseButtonEditable(Project project, JTable jTable) {
        textFieldWithBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String file = FileChooseUtils.chooseSingleFile(project, null, null);
                if (file != null) {
                    int editingRow = jTable.getEditingRow();
                    jTable.setValueAt(file, editingRow, jTable.getEditingColumn());
                    textFieldWithBrowseButton.setText(file);
                }
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textFieldWithBrowseButton.setText(value.toString());
        return textFieldWithBrowseButton;
    }

    @Override
    public Object getCellEditorValue() {
        return textFieldWithBrowseButton.getText();
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        return true;
    }

    @Override
    public void cancelCellEditing() {

    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {

    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {

    }
}
