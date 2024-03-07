package com.cool.request.view.page.cell;

import com.cool.request.view.widget.AutocompleteField;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TextFieldWithAutoCompletion;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventObject;

public class DefaultJTextCellEditable extends DefaultCellEditor {
    private AutocompleteField textFieldWithAutoCompletion;

    public DefaultJTextCellEditable(AutocompleteField jTextField, Project project) {
        super(new JTextField());
        this.textFieldWithAutoCompletion = jTextField;
    }

    public DefaultJTextCellEditable(Project project) {
        this(new AutocompleteField(null), project);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component tableCellEditorComponent = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        tableCellEditorComponent.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return textFieldWithAutoCompletion;
    }

    @Override
    public Object getCellEditorValue() {
        return textFieldWithAutoCompletion.getText();
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }


}
