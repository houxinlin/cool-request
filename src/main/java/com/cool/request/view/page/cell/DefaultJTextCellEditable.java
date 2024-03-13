package com.cool.request.view.page.cell;

import com.cool.request.view.widget.AutocompleteField;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;

public class DefaultJTextCellEditable extends DefaultCellEditor {
    private final AutocompleteField textFieldWithAutoCompletion;

    public DefaultJTextCellEditable(AutocompleteField jTextField) {
        super(jTextField);
        this.textFieldWithAutoCompletion = jTextField;
    }

    public DefaultJTextCellEditable() {
        this(new AutocompleteField(null));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component tableCellEditorComponent = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        tableCellEditorComponent.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        textFieldWithAutoCompletion.setText(value == null ? "" : value.toString());
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
