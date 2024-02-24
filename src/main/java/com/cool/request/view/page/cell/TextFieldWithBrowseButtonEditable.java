package com.cool.request.view.page.cell;

import com.cool.request.utils.file.FileChooseUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;

public class TextFieldWithBrowseButtonEditable extends DefaultCellEditor {
    private final TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();

    public TextFieldWithBrowseButtonEditable(Project project, JTable jTable) {
        super(new JTextField());
        textFieldWithBrowseButton.addActionListener(e -> {
            String file = FileChooseUtils.chooseDirectory(project);
            if (file != null) {
                int editingRow = jTable.getEditingRow();
                jTable.setValueAt(file, editingRow, jTable.getEditingColumn());
                textFieldWithBrowseButton.setText(file);
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
        return textFieldWithBrowseButton.getText();
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
