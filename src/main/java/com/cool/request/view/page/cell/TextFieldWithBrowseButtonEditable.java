package com.cool.request.view.page.cell;

import com.cool.request.utils.file.FileChooseUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.fields.ExtendableTextField;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

public class TextFieldWithBrowseButtonEditable extends DefaultCellEditor {
    private final ExtendableTextField field = new ExtendableTextField();
    private final TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton(field);

    public TextFieldWithBrowseButtonEditable(Project project, JTable jTable) {
        super(new JTextField());
        textFieldWithBrowseButton.addActionListener(e -> {
            String file = FileChooseUtils.chooseDirectory(project);
            if (file != null) {
                int editingRow = jTable.getEditingRow();
                jTable.setValueAt(file, editingRow, jTable.getEditingColumn());
                textFieldWithBrowseButton.setText(file);
                field.setText(file);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textFieldWithBrowseButton.setText(value.toString());
        textFieldWithBrowseButton.setBackground(isSelected?table.getSelectionBackground():table.getBackground());
        textFieldWithBrowseButton.setEnabled(!Boolean.parseBoolean(table.getValueAt(row, 0).toString()));
        return textFieldWithBrowseButton;
    }

    @Override
    public Object getCellEditorValue() {
        return field.getText();
    }

    @Override
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }


    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

}
